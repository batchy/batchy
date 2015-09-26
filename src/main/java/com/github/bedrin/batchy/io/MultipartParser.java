package com.github.bedrin.batchy.io;

import com.github.bedrin.batchy.util.MultiHashMap;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

public class MultipartParser {

    private final String boundary;
    private final HttpRequestProcessor httpRequestProcessor;

    public MultipartParser(String boundary, HttpRequestProcessor httpRequestProcessor) {
        this.boundary = "--" + boundary;
        this.httpRequestProcessor = httpRequestProcessor;
    }

    public void parseMultipartRequest(InputStream inputStream) throws IOException, ServletException {

        HeaderParser headerParser = new HeaderParser(false);

        PushbackInputStream pis = new PushbackInputStream(inputStream, boundary.length());

        // preface
        byte[] buff = new byte[boundary.length()];
        int bytesRead = 0;
        for (int i = 0; i != -1 && bytesRead < boundary.length();
             bytesRead += (i = pis.read(buff, bytesRead, boundary.length() - bytesRead)));

        if (bytesRead == boundary.length()) {

            if (!Arrays.equals(buff, boundary.getBytes())) {
                // preface; push back the first bytes and drain input stream till boundary met
                pis.unread(buff);
                drainInputStream(new BoundedInputStream(pis, boundary.getBytes(), BoundedInputStream.Prefix.NEW_LINE));
            }

            // parts
            do {
                if (wasLastPart(pis)) break;
                BoundedInputStream bis = new BoundedInputStream(pis, boundary.getBytes(), BoundedInputStream.Prefix.NEW_LINE);
                MultiHashMap<String, String> messageHeaders = headerParser.parseHeader(bis);
                String requestLine = headerParser.readFirstNotEmptyLine(bis);
                MultiHashMap<String, String> httpHeaders = headerParser.parseHeader(bis);
                httpRequestProcessor.processHttpRequest(messageHeaders, requestLine, httpHeaders, bis);

            } while (true);

            // epilogue
            drainInputStream(pis); // todo do we really need to drain it?

        }

    }

    private boolean wasLastPart(PushbackInputStream pis) throws IOException {
        int a = pis.read();
        if (-1 == a) {
            return true;
        } else if ('-' == a) {
            int b = pis.read();
            if (-1 == b) {
                return true;
            } else if ('-' == b) {
                return true;
            } else {
                pis.unread(a);
                pis.unread(b);
            }
        } else {
            pis.unread(a);
        }
        return false;
    }

    private void drainInputStream(InputStream is) throws IOException {
        byte[] buffer = new byte[8192];
        while (is.read(buffer) != -1);
    }

}
