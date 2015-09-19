package com.github.bedrin.batchy.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

// todo implement rest of the methods
public class PartServletResponse extends HttpServletResponseWrapper {

    public PartServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletPartOutputStream(new ByteArrayOutputStream());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(getOutputStream());
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    private static class ServletPartOutputStream extends ServletOutputStream {

        private final OutputStream os;

        public ServletPartOutputStream(OutputStream os) {
            this.os = os;
        }

        @Override
        public void write(int b) throws IOException {
            os.write(b);
        }

    }

}
