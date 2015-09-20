package com.github.bedrin.batchy.wrapper;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DelegatingServletOutputStream extends ServletOutputStream {

    private final OutputStream os;

    public DelegatingServletOutputStream(OutputStream os) {
        this.os = os;
    }

    @Override
    public void write(int b) throws IOException {
        os.write(b);
    }

}
