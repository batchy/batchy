package com.github.bedrin.batchy.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrefetchInputStream extends BufferedInputStream {

    public PrefetchInputStream(InputStream in) {
        super(in);
    }

    public PrefetchInputStream(InputStream in, int size) {
        super(in, size);
    }

    public boolean prefetch() throws IOException {
        int size = buf.length;
        mark(size);
        try {
            int i = 0;
            while (i < size && (read() != -1)) {
                i++;
            }
            return (i < size);
        } finally {
            reset();
        }
    }


}
