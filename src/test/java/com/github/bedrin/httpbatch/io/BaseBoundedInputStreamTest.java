package com.github.bedrin.httpbatch.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseBoundedInputStreamTest {

    protected static final byte[] BOUNDARY = "--bound".getBytes();
    protected static final String SAMPLE0_NO_DELIMETER = "foo\r\nbar\r\nbaz\r\n--notbound\r\n42";
    protected static final String SAMPLE1_CRLF_DELIMETER = "foo\r\nbar\r\nbaz\r\n--bound\r\n42";
    protected static final String SAMPLE2_CR_DELIMETER = "foo\rbar\nbaz\r--bound\r\n42";
    protected static final String SAMPLE3_LF_DELIMETER = "foo\nbar\rbaz\n--bound\r\n42";
    protected static final String SAMPLE4_DOUBLE_CRLF_DELIMETER = "foo\r\nbar\r\nbaz\r\n\r\n--bound\r\n42";

    protected String readStreamToString(InputStream bis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = bis.read()) != -1) {
            baos.write(i);
        }
        return new String(baos.toByteArray());
    }
}
