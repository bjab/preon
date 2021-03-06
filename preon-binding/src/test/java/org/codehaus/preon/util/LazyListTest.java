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
package org.codehaus.preon.util;

import junit.framework.TestCase;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.el.Expression;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LazyListTest {

    private BitBuffer buffer;

    private Codec codec;

    private Builder builder;

    private Resolver resolver;

    private Expression<Integer, Resolver> sizeExpr;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        buffer = mock(BitBuffer.class);
        codec = mock(Codec.class);
        builder = mock(Builder.class);
        resolver = mock(Resolver.class);
        sizeExpr = mock(Expression.class);
    }

    @Test
    public void testTakingElement() throws DecodingException {
        Object value = new Object();
        buffer.setBitPos(20);
        when(codec.decode(buffer, resolver, builder)).thenReturn(value);

        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 10, builder, resolver, 20);
        list.get(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexToLow() {
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                    codec, 0, buffer, 10, builder, resolver, 20);
        list.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexToHigh() {
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                    codec, 0, buffer, 10, builder, resolver, 20);
        list.get(10);
    }

    @Test
    public void testSubList() throws DecodingException {
        Object value = new Object();
        buffer.setBitPos(20);
        when(codec.decode(buffer, resolver, builder)).thenReturn(value);

        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 10, builder, resolver, 20);
        List<Object> sublist = list.subList(1, 3);
        sublist.get(0);
    }

    @Test
    public void testIterator() throws DecodingException {
        Object value = new Object();
        buffer.setBitPos(0);
        when(codec.decode(buffer, resolver, builder)).thenReturn(value);
        buffer.setBitPos(20);
        when(codec.decode(buffer, resolver, builder)).thenReturn(value);
        buffer.setBitPos(40);
        when(codec.decode(buffer, resolver, builder)).thenReturn(value);

        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 3, builder, resolver, 20);
        Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

}
