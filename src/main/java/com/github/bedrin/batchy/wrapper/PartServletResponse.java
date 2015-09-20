package com.github.bedrin.batchy.wrapper;

import com.github.bedrin.batchy.io.CollectingOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

// todo implement rest of the methods
public class PartServletResponse extends HttpServletResponseWrapper implements CollectingOutputStream.FlushListener {

    public PartServletResponse(HttpServletResponse response) {
        super(response);
    }

    // flush and commit logic

    private boolean committed;

    @Override
    public boolean isCommitted() {
        return committed;
    }

    protected void setCommitted(boolean committed) {
        this.committed = committed;
    }

    protected void setCommitted() {
        setCommitted(true);
    }

    @Override
    public void onBeforeFlush() {

    }


    // output stream

    private ServletOutputStream outputStream;
    private PrintWriter writer;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (null != outputStream) {
            return outputStream;
        } else if (null != writer) {
            throw new IllegalStateException("getWriter() method has been called on this response");
        } else {
            ServletOutputStream target = getResponse().getOutputStream();
            CollectingOutputStream cos = new CollectingOutputStream(target);
            cos.addListener(this);
            return outputStream = new DelegatingServletOutputStream(cos);
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (null != writer) {
            return writer;
        } else if (null != outputStream) {
            throw new IllegalStateException("getOutputStream() method has been called on this response");
        } else {
            return writer = new PrintWriter(getOutputStream());
        }
    }

    //

    private int statusCode;
    private String responseMessage;

    @Override
    public void sendError(int sc, String msg) throws IOException {
        super.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        super.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        super.sendRedirect(location);
    }


}
