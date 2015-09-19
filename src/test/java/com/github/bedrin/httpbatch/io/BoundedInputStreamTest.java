package com.github.bedrin.httpbatch.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class BoundedInputStreamTest {

    private static final String SAMPLE1_CRLF_DELIMETER = "foo\r\nbar\r\nbaz\r\n--bound\r\n42";
    private static final String SAMPLE2_CR_DELIMETER = "foo\rbar\nbaz\r--bound\r\n42";
    private static final String SAMPLE3_LF_DELIMETER = "foo\nbar\rbaz\n--bound\r\n42";
    private static final String SAMPLE4_DOUBLE_CRLF_DELIMETER = "foo\r\nbar\r\nbaz\r\n\r\n--bound\r\n42";
    public static final byte[] BOUNDARY = "--bound".getBytes();

    @Test
    public void testReadSample1CrLfDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE1_CRLF_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.NEW_LINE);
        assertEquals("foo\r\nbar\r\nbaz", readStreamToString(bis));
        assertEquals("\r\n42", readStreamToString(in));
    }

    @Test
    public void testReadSample2CrDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE2_CR_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.NEW_LINE);
        assertEquals("foo\rbar\nbaz", readStreamToString(bis));
        assertEquals("\r\n42", readStreamToString(in));
    }

    @Test
    public void testReadSample3LfDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE3_LF_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.NEW_LINE);
        assertEquals("foo\nbar\rbaz", readStreamToString(bis));
        assertEquals("\r\n42", readStreamToString(in));
    }

    @Test
    public void testReadSample4DoubleCrLfDelimeter() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(SAMPLE4_DOUBLE_CRLF_DELIMETER.getBytes());
        InputStream bis = new BoundedInputStream(in, BOUNDARY, BoundedInputStream.Prefix.NEW_LINE);
        assertEquals("foo\r\nbar\r\nbaz\r\n", readStreamToString(bis));
        assertEquals("\r\n42", readStreamToString(in));
    }

    private String readStreamToString(InputStream bis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = bis.read()) != -1) {
            baos.write(i);
        }
        return new String(baos.toByteArray());
    }

}