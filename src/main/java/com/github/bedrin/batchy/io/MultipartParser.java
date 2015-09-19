package com.github.bedrin.batchy.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Map;

public class MultipartParser {

    private final String boundary;
    private final HttpRequestProcessor httpRequestProcessor;

    public MultipartParser(String boundary, HttpRequestProcessor httpRequestProcessor) {
        this.boundary = "--" + boundary;
        this.httpRequestProcessor = httpRequestProcessor;
    }

    public void parseMultipartRequest(InputStream inputStream) throws IOException {

        HeaderParser headerParser = new HeaderParser(false);

        PushbackInputStream pis = new PushbackInputStream(inputStream, 2);

        // preface
        {
            pis.unread('\n'); // todo workaround for no preface
            drainInputStream(new BoundedInputStream(pis, boundary.getBytes(), BoundedInputStream.Prefix.NEW_LINE));
        }

        // parts
        do {
            if (wasLastPart(pis)) break;
            BoundedInputStream bis = new BoundedInputStream(pis, boundary.getBytes(), BoundedInputStream.Prefix.NEW_LINE);
            Map<String, String> messageHeaders = headerParser.parseHeader(bis);
            String requestLine = headerParser.readFirstNotEmptyLine(bis);
            Map<String, String> httpHeaders = headerParser.parseHeader(bis);
            httpRequestProcessor.processHttpRequest(messageHeaders, requestLine, httpHeaders, bis);
            drainInputStream(bis); // todo do we need this precaution?
        } while (true);

        // epilogue
        drainInputStream(pis); // todo do we really need to drain it?

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
        while (is.read() != -1);
    }

}
