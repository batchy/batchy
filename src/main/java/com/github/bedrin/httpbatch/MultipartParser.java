package com.github.bedrin.httpbatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

public class MultipartParser {

    private static final int PRE = 0;
    private static final int PART = 1;
    private static final int PART_BODY = 2;
    public static final int CLOSED = -1;

    private final String boundary;

    public MultipartParser(String boundary) {
        this.boundary = "--" + boundary;
    }

    public void parseMultipartRequest(InputStream inputStream) throws IOException {

        PushbackInputStream pis = new PushbackInputStream(inputStream, 1);

        int state = PRE;

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        while (state != CLOSED) switch (state) {
            case PRE: {

                int i;
                switch (i = pis.read()) {
                    case -1: state = CLOSED; break;
                    case '\r': if ((i = pis.read()) != '\n') pis.unread(i);
                    case '\n': {
                        if (buff.size() == boundary.length() && Arrays.equals(buff.toByteArray(),boundary.getBytes())) state = PART;
                        else if (buff.size() > 0) System.out.println("Pre: " + buff.toString());
                        buff.reset();
                        break;
                    }
                    default:
                        buff.write(i);
                }
                break;
            }
            case PART: {
                int i;
                switch (i = pis.read()) {
                    case -1  : state = CLOSED; break;
                    case '\r': if ((i = pis.read()) != '\n') pis.unread(i);
                    case '\n': {
                        if (0 == buff.size()) state = PART_BODY;
                        else System.out.println("Part Header: " + buff.toString());
                        buff.reset();
                        break;
                    }
                    default:
                        buff.write(i);
                }
                break;
            }
            case PART_BODY: {
                // TODO: fail back to PartInputStream in case of large input
                int i;
                switch (i = pis.read()) {
                    case -1  :
                        System.out.println("Part Body: " + data.toString());
                        state = CLOSED;
                        break;
                    case '\r': if ((i = pis.read()) != '\n') pis.unread(i);
                    case '\n':
                        if (buff.size() == boundary.length() && Arrays.equals(buff.toByteArray(),boundary.getBytes())) {
                            System.out.println("Part Body: " + data.toString());
                            state = PART;
                        }
                        else {
                            buff.writeTo(data);
                        }
                        buff.reset();
                        break;
                    default:
                        buff.write(i);
                }
                break;
            }
        }
    }

}
