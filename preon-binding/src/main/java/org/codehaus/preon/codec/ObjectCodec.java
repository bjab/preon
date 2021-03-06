/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.preon.codec;

import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.ObjectResolverContext;

import java.io.IOException;
import java.util.List;

/**
 * <p>The {@link Codec} capable of decoding instances of arbitrary classes. Typicaly, this {@link Codec} will be
 * constructed using the {@link ObjectCodecFactory} companion class that's embedded in the definition of this class. If
 * you do so, then the bindings will be based on the presence of annotations on the fields of the class for which you
 * need a {@link Codec}.</p>
 */
public class ObjectCodec<T> implements Codec<T> {

    private final Class<T> type;

    private final ObjectResolverContext context;

    /**
     *
     * @param type
     * @param context
     */
    public ObjectCodec(Class<T> type, ObjectResolverContext context) {
        assert type != null;
        assert context != null;
        this.type = type;
        this.context = context;
    }

    public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException {
        assert buffer != null;
        assert builder != null;
        try {
            final T result = builder.create(type);
            resolver = context.getResolver(result, resolver);
            // TODO: I think I need a replacement resolver here.
            for (Binding binding : context.getBindings()) {
                binding.load(result, buffer, resolver, builder);
            }
            return result;
        }
        catch (InstantiationException ie) {
            throw new DecodingException(type, ie);
        }
        catch (IllegalAccessException iae) {
            throw new DecodingException(iae);
        }
    }

    public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
        resolver = context.getResolver(value, resolver);
        for (Binding binding : context.getBindings()) {
            binding.save(value, channel, resolver);
        }
    }

    public Class<?>[] getTypes() {
        return new Class[]{type};
        // Set<Class<?>> types = new HashSet<Class<?>>();
        // for (Binding binding : context.getBindings()) {
        // types.addAll(Arrays.asList(binding.getTypes()));
        // }
        // types.add(type);
        // return new ArrayList<Class<?>>(types).toArray(new Class[0]);
    }

    /*
       * (non-Javadoc)
       *
       * @see org.codehaus.preon.Codec#getSize()
       */

    public Expression<Integer, Resolver> getSize() {
        List<Binding> bindings = context.getBindings();
        if (bindings.size() > 0) {
            Expression<Integer, Resolver> result = null;
            for (Binding binding : bindings) {
                if (result == null) {
                    result = binding.getSize();
                } else {
                    result = Expressions.add(result, binding.getSize());
                }
            }
            return result;
        } else {
            return Expressions.createInteger(0, Resolver.class);
        }
    }

    public String toString() {
        return "Codec of " + type.getSimpleName();
    }

    public Class<?> getType() {
        return type;
    }
}
