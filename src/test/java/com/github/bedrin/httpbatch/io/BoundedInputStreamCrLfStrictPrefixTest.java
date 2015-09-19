package com.github.bedrin.httpbatch.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class BoundedInputStreamCrLfStrictPrefixTest extends BaseBoundedInputStreamTest {

    @Test
    public void testReadSample0NoDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE0_NO_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.CRLF_STRICT);
        assertEquals("foo\r\nbar\r\nbaz\r\n--notbound\r\n42", readStreamToString(bis));
        assertEquals("", readStreamToString(in));
    }

    @Test
    public void testReadSample1CrLfDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE1_CRLF_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.CRLF_STRICT);
        assertEquals("foo\r\nbar\r\nbaz", readStreamToString(bis));
        assertEquals("\r\n42", readStreamToString(in));
    }

    @Test
    public void testReadSample2CrDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE2_CR_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.CRLF_STRICT);
        assertEquals("foo\rbar\nbaz\r--bound\r\n42", readStreamToString(bis));
        assertEquals("", readStreamToString(in));
    }

    @Test
    public void testReadSample3LfDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE3_LF_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.CRLF_STRICT);
        assertEquals("foo\nbar\rbaz\n--bound\r\n42", readStreamToString(bis));
        assertEquals("", readStreamToString(in));
    }

    @Test
    public void testReadSample4DoubleCrLfDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE4_DOUBLE_CRLF_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.CRLF_STRICT);
        assertEquals("foo\r\nbar\r\nbaz\r\n", readStreamToString(bis));
        assertEquals("\r\n42", readStreamToString(in));
    }

}