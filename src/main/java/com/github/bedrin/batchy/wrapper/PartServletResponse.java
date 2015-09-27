package com.github.bedrin.batchy.wrapper;

import com.github.bedrin.batchy.io.LazyOutputStream;
import com.github.bedrin.batchy.mux.AsyncMultiplexer;
import com.github.bedrin.batchy.util.DateUtils;
import com.github.bedrin.batchy.util.MultiHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

// todo implement rest of the methods
public class PartServletResponse extends HttpServletResponseWrapper implements LazyOutputStream.OutputStreamSupplier {

    private final AsyncMultiplexer multiplexer;
    private final String boundary;

    public PartServletResponse(AsyncMultiplexer multiplexer, HttpServletResponse response, String boundary) {
        super(response);
        this.multiplexer = multiplexer;
        this.boundary = boundary;
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

    // flushing and streams

    private boolean headersSent = false; // todo can we use committed instead

    @Override
    public OutputStream get() throws IOException {
        multiplexer.getResponseLock().lock();
        final ServletOutputStream outputStream = getResponse().getOutputStream();
        if (!headersSent) {
            commitHeaders(outputStream);
            headersSent = true;
        }
        return outputStream;
    }

    private void commitHeaders(ServletOutputStream sos) {
        PrintWriter pw = new PrintWriter(sos);

        if (multiplexer.getFinishedRequests() > 0) {
            pw.append("\r\n");
        }

        pw.append("--").append(boundary).append("\r\n").append("Content-Type: application/http; msgtype=response\r\n\r\n");

        pw.
                append("HTTP/1.1"). // todo take protocol version from request
                append(" ").
                append(Integer.toString(statusCode)).
                append(" ");

        if (null != statusMessage) {
            pw.append(" ").append(statusMessage);
        }

        pw.append("\r\n");

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                pw.append(entry.getKey()).append(": ").append(value).append("\r\n");
            }
        }

        pw.append("\r\n");

        pw.flush();
    }

    private LazyOutputStream lazyOutputStream;
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (null != outputStream) {
            return outputStream;
        } else if (null != writer) {
            throw new IllegalStateException("getWriter() method has been called on this response");
        } else {
            return outputStream = new DelegatingServletOutputStream(lazyOutputStream = new LazyOutputStream(this));
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (null != writer) {
            return writer;
        } else if (null != outputStream) {
            throw new IllegalStateException("getOutputStream() method has been called on this response");
        } else {
            return writer = new PrintWriter(lazyOutputStream = new LazyOutputStream(this));
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        if (null != writer) {
            writer.flush();
        } else if (null != outputStream) {
            outputStream.flush();
        }

        setCommitted();
    }

    @Override
    public void resetBuffer() {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        lazyOutputStream.reset();
    }

    @Override
    public void reset() {
        resetBuffer();
        // todo reset headers and stuff
        headers.clear();
        statusCode = SC_OK;
        statusMessage = null;
    }

    @Override
    public int getBufferSize() {
        return lazyOutputStream.getSize();
    }

    @Override
    public void setBufferSize(int size) {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        // todo implement; can do nothing so far
    }

    //

    private int statusCode = HttpServletResponse.SC_OK;
    private String statusMessage;

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        setStatus(sc);
        this.statusMessage = sm;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        setStatus(sc, msg);
        get();
        setCommitted();
    }

    @Override
    public void sendError(int sc) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        setStatus(sc);
        get();
        setCommitted();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        if (isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        addHeader("Location", location);
        get();
        setCommitted();
    }

    // Headers

    private MultiHashMap<String, String> headers = new MultiHashMap<String, String>();

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public void setDateHeader(String name, long date) {
        headers.put(name, Arrays.asList(DateUtils.formatDate(new Date(date))));
    }

    @Override
    public void addDateHeader(String name, long date) {
        headers.add(name, DateUtils.formatDate(new Date(date)));
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, Arrays.asList(value));
    }

    @Override
    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        headers.put(name, Arrays.asList(Integer.toString(value)));
    }

    @Override
    public void addIntHeader(String name, int value) {
        headers.add(name, Integer.toString(value));
    }

    // todo implement headers (and charset) related methods

    @Override
    public void setCharacterEncoding(String charset) {
        super.setCharacterEncoding(charset);
    }

    @Override
    public String getCharacterEncoding() {
        return super.getCharacterEncoding();
    }

    @Override
    public void setContentLength(int len) {
        super.setContentLength(len);
    }

    @Override
    public void setContentType(String type) {
        setHeader("Content-Type", type);
    }

    @Override
    public String getContentType() {
        return super.getContentType();
    }

    @Override
    public void setLocale(Locale loc) {
        super.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return super.getLocale();
    }

    // todo implement cookies methods


    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
    }
}
