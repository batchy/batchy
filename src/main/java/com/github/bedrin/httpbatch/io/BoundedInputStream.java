package com.github.bedrin.httpbatch.io;

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

    private boolean closed = false;
    private int pos = 0;
    private int size = 0;

    public BoundedInputStream(InputStream in, byte[] boundary) {
        this.pis = new PushbackInputStream(in);
        this.boundary = boundary;

        this.buf = new byte[boundary.length + 2];
    }

    @Override
    public int read() throws IOException {

        ensureOpen();

        if (pos < size) {
            return buf[pos++];
        } else {

            // reset buffer position and size
            pos = size = 0;

            // read new character from underlying stream
            int i = pis.read();

            // if new character
            if (i == '\r') {
                buf[size++] = (byte) i;
                if ((i = pis.read()) != '\n') pis.unread(i);
            }
            if (i == '\n') {
                buf[size++] = (byte) i;
            }

            if (size > 0) {
                // start of new line
                int boundaryPos = 0;
                while (
                        boundaryPos < boundary.length &&
                        (i = pis.read()) == boundary[boundaryPos++] ) {
                    buf[size++] = (byte) i;
                }
                if (boundaryPos == boundary.length) {
                    // match
                    close();
                    return -1;
                } else {
                    // push back \r and \n characters
                    if ('\r' == i || '\n' == i) pis.unread(i);
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