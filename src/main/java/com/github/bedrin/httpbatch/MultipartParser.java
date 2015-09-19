package com.github.bedrin.httpbatch;

import com.github.bedrin.httpbatch.io.BoundedInputStream;
import com.github.bedrin.httpbatch.io.HeaderParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Map;

public class MultipartParser {

    private final String boundary;

    public MultipartParser(String boundary) {
        this.boundary = "--" + boundary;
    }

    private void drainInputStream(InputStream is) throws IOException {
        while (is.read() != -1);
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
            //dump(httpHeaders);
            //printInputStream(bis);
        } while (true);

        // epilogue
        drainInputStream(pis); // todo do we really need to drain it?

    }

    private void dump(Map<String,String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
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

}
