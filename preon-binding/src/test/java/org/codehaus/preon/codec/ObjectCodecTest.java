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

import org.codehaus.preon.Resolver;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.ObjectResolverContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

// TODO unmock this test - this could be easily done with actual bindings in the TestSubject class
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ObjectCodecTest {

    @Mock
    private ObjectResolverContext context;

    @Mock
    private Binding binding1;

    @Mock
    private Binding binding2;

    @Mock
    private BitChannel channel;

    @Mock
    private Resolver resolver;

    private List<Binding> listOfBindings;

    @Before
    public void prepareListOfBindings() {
        listOfBindings = Arrays.asList(binding1, binding2);
    }

    @Test
    public void shouldEncodeAllFields() throws IOException {
        ObjectCodec<TestSubject> codec = new ObjectCodec<TestSubject>(TestSubject.class, context);
        TestSubject value = new TestSubject();
        when(context.getBindings()).thenReturn(listOfBindings);
        codec.encode(value, channel, resolver);
        verify(binding1).save(Mockito.eq(value), Mockito.eq(channel), Mockito.<Resolver>any(Resolver.class));
        verify(binding2).save(Mockito.eq(value), Mockito.eq(channel), Mockito.<Resolver>any(Resolver.class));
        verifyNoMoreInteractions(binding1, binding2);
    }

    private static class TestSubject {

    }

}
