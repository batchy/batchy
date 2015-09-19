package com.github.bedrin.httpbatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class HttpRequestParser {

    private static final int PRE = 0;
    private static final int PART = 1;
    private static final int PART_BODY = 2;
    public static final int CLOSED = -1;

    private final String boundary;

    public HttpRequestParser(String boundary) {
        this.boundary = "--" + boundary;
    }

    public void parseMultipartRequest(InputStream inputStream) throws IOException {

        PushbackInputStream pis = new PushbackInputStream(inputStream, 1);

        int state = PRE;

        StringBuilder sb = new StringBuilder();

        while (state != CLOSED) switch (state) {
            case PRE: {

                int i;
                switch (i = pis.read()) {
                    case -1: state = CLOSED; break;
                    case '\r': if ((i = pis.read()) != '\n') pis.unread(i);
                    case '\n': {
                        if (sb.toString().equals(boundary)) state = PART;
                        else System.out.println("Pre: " + sb.toString());
                        sb = new StringBuilder();
                        break;
                    }
                    default:
                        sb.appendCodePoint(i);
                }
                break;
            }
            case PART: {
                int i;
                switch (i = pis.read()) {
                    case -1: state = CLOSED; break;
                    case '\r': if ((i = pis.read()) != '\n') pis.unread(i);
                    case '\n': {
                        if (sb.toString().trim().isEmpty()) state = PART_BODY;
                        System.out.println("Part Header: " + sb.toString());
                        sb = new StringBuilder();
                        break;
                    }
                    default:
                        sb.appendCodePoint(i);
                }
                break;
            }
            case PART_BODY: {
                int i;
                switch (i = pis.read()) {
                    case -1: state = CLOSED; break;
                    case '\r': if ((i = pis.read()) != '\n') pis.unread(i);
                    case '\n': {
                        if (sb.toString().equals(boundary)) state = PART;
                        else System.out.println("Part Body: " + sb.toString());
                        sb = new StringBuilder();
                        break;
                    }
                    default:
                        sb.appendCodePoint(i);
                }
                break;
            }
        }
    }

}
