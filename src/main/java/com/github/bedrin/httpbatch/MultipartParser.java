package com.github.bedrin.httpbatch;

import com.github.bedrin.httpbatch.io.BoundedInputStream;
import com.github.bedrin.httpbatch.io.HeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.Map;

public class MultipartParser {

    private final String boundary;

    public MultipartParser(String boundary) {
        this.boundary = "--" + boundary;
    }

    private void drainInputStream(InputStream is) throws IOException {
        while (is.read() != -1);
    }

    private byte[] printInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        System.out.print(new String(baos.toByteArray()));
        return baos.toByteArray();
    }

    public void parseMultipartRequest(InputStream inputStream) throws IOException {

        PushbackInputStream pis = new PushbackInputStream(inputStream);

        // preface
        {
            pis.unread('\n');
            BoundedInputStream bis = new BoundedInputStream(pis, boundary.getBytes(), BoundedInputStream.Prefix.NEW_LINE);
            System.out.print("<PREFACE>");
            printInputStream(bis);
            System.out.println("</PREFACE>");
        }

        // parts
        do {
            BoundedInputStream bis = new BoundedInputStream(pis, boundary.getBytes(), BoundedInputStream.Prefix.NEW_LINE);
            System.out.print("<PART>");
            final HeaderParser headerParser = new HeaderParser(false);
            for (Map.Entry<String, String> entry : headerParser.parseHeader(bis).entrySet()) {
                System.out.println("Header: " + entry.getKey() + ": " + entry.getValue());
            }
            if (printInputStream(bis).length == 0) break;
            System.out.println("</PART>");
        } while (true);

    }

}
