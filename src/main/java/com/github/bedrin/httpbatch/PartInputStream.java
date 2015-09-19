package com.github.bedrin.httpbatch;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream reads data from underlying stream until boundary is met or
 * number of read bytes exceeds provided limit
 */
public class PartInputStream extends InputStream {

    private final InputStream in;
    private final byte[] boundary;
    private final int limit;

    public PartInputStream(InputStream in, byte[] boundary, int limit) {
        this.in = in;
        this.boundary = boundary;
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

}