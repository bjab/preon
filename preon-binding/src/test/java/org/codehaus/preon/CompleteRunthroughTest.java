package org.codehaus.preon;

import org.apache.commons.codec.binary.Hex;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.buffer.ByteOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Just a simple smoke test.
 *
 * @author Thorsten Frank
 */
public class CompleteRunthroughTest {

    // Big Endian
    private static final String SAMPLE_HEX = "0036666f6f2c206261722c2062617a000000";
    //"3600666f6f2c206261722c2062617a000000" ; // little endian

    @Rule
    public ExpectedException rule = ExpectedException.none();

    @Test
    public void testEncode() throws IOException  {
        TestSubject testSubject = new TestSubject();
        testSubject.someNumber = 54;
        testSubject.someString = "foo, bar, baz";

        DefaultCodecFactory codecFactory = new DefaultCodecFactory();
        Codec<TestSubject> codec = codecFactory.create(TestSubject.class);

        byte[] result = Codecs.encode(testSubject, codec);

        assertEquals(SAMPLE_HEX, Hex.encodeHexString(result));
    }

    @Test
    public void testDecode() throws Exception {
        DefaultCodecFactory codecFactory = new DefaultCodecFactory();
        Codec<TestSubject> codec = codecFactory.create(TestSubject.class);

        TestSubject testSubject = Codecs.decode(codec, Hex.decodeHex(SAMPLE_HEX));
        assertEquals(54, testSubject.someNumber);
        assertEquals("foo, bar, baz", testSubject.someString);
    }

    @Test
    public void testDecodeUnderflowExceptionIncludesFieldAndTypeInformation() throws Exception {
        rule.expect(DecodingException.class);
        rule.expectMessage("Error decoding field [someString] in type [TestSubject2]");
        DefaultCodecFactory codecFactory = new DefaultCodecFactory();
        Codec<TestSubject2> codec = codecFactory.create(TestSubject2.class);

        Codecs.decode(codec, Hex.decodeHex(SAMPLE_HEX));
    }

    @Test
    public void testValueOutOfRange() throws Exception {
        TestSubject testSubject = new TestSubject();
        testSubject.someNumber = Integer.MAX_VALUE;
        testSubject.someString = "irrelevant";

        DefaultCodecFactory codecFactory = new DefaultCodecFactory();
        Codec<TestSubject> codec = codecFactory.create(TestSubject.class);

        byte[] encoded = Codecs.encode(testSubject, codec);

        TestSubject decoded = Codecs.decode(codec, encoded);
        System.out.println(decoded.someNumber);
    }

    static class TestSubject {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int someNumber;

        @BoundString(size = "2*8", encoding = "UTF-8")
        private String someString;
    }

    static class TestSubject2 {
        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int someNumber;

        @BoundString(size = "3*8", encoding = "UTF-8")
        private String someString;
    }
}
