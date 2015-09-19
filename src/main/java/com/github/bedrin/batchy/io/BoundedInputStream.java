package com.github.bedrin.batchy.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * InputStream reads data from underlying stream until boundary is met or
 * number of read bytes exceeds provided limit
 */
public class BoundedInputStream extends InputStream {

    private final PushbackInputStream pis;
    private final byte[] boundary;

    private final byte[] buf;

    private final Prefix prefix;

    public enum Prefix {
        NONE, CRLF_STRICT, NEW_LINE
    }

    private boolean closed = false;
    private boolean empty = false;
    private int pos = 0;
    private int size = 0;

    public BoundedInputStream(InputStream in, byte[] boundary, Prefix prefix) {
        this.pis = new PushbackInputStream(in);
        this.boundary = boundary;
        this.prefix = prefix;
        this.buf = new byte[boundary.length + 2];
    }

    @Override
    public int read() throws IOException {

        ensureOpen();

        if (empty) return -1;

        if (pos < size) {
            return buf[pos++];
        } else {

            // reset buffer position and size
            pos = size = 0;

            // read new character from underlying stream
            int i = pis.read();

            // if new character
            if (prefix == Prefix.NEW_LINE) {
                if (i == '\r') {
                    buf[size++] = (byte) i;
                    if ((i = pis.read()) != '\n') pis.unread(i);
                }
                if (i == '\n') {
                    buf[size++] = (byte) i;
                }
            } else if (prefix == Prefix.CRLF_STRICT) {
                if (i == '\r') {
                    if ((i = pis.read()) == '\n') {
                        buf[size++] = '\r';
                        buf[size++] = '\n';
                    } else {
                        pis.unread(i);
                        i = '\r';
                    }
                }
            } else {
                pis.unread(i);
            }

            if (size > 0 || prefix == Prefix.NONE) {
                // start of new line
                int boundaryPos = 0;
                while (
                        boundaryPos < boundary.length &&
                        (i = pis.read()) == boundary[boundaryPos++] ) {
                    buf[size++] = (byte) i;
                }
                if (boundaryPos == boundary.length) {
                    // match
                    empty = true;
                    return -1;
                } else {
                    // push back \r and \n characters
                    if (prefix == Prefix.NEW_LINE && ('\r' == i || '\n' == i))
                        pis.unread(i);
                    else if (prefix == Prefix.CRLF_STRICT && '\r' == i)
                        pis.unread(i);
                    else buf[size++] = (byte) i;
                }
                return buf[pos++];
            } else {
                // just return the character
                return i;
            }

        }

    }



    @Override
    public int available() throws IOException {
        ensureOpen();
        int n = buf.length - pos;
        int avail = super.available();
        return n > (Integer.MAX_VALUE - avail)
                ? Integer.MAX_VALUE
                : n + avail;
    }

    private void ensureOpen() throws IOException {
        if (closed) throw new IOException("Stream closed");
    }

    public void close() throws IOException {
        closed = true;
    }

}