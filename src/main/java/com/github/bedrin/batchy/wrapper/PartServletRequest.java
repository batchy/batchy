package com.github.bedrin.batchy.wrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// todo implement rest of the methods
public class PartServletRequest extends HttpServletRequestWrapper {

    private final static String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    private final static String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    private final static String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
    private final static String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    private final static String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";

    public PartServletRequest(HttpServletRequest request) {
        super(request);
    }

    private String method;
    private InputStream inputStream;

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletPartInputStream(inputStream);
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream())); // todo consider encoding
    }

    @Override
    public String getContextPath() {
        return (String) getRequest().getAttribute(INCLUDE_CONTEXT_PATH);
    }

    @Override
    public String getPathInfo() {
        return (String) getRequest().getAttribute(INCLUDE_PATH_INFO);
    }

    @Override
    public String getQueryString() {
        return (String) getRequest().getAttribute(INCLUDE_QUERY_STRING);
    }

    @Override
    public String getRequestURI() {
        return (String) getRequest().getAttribute(INCLUDE_REQUEST_URI);
    }

    @Override
    public StringBuffer getRequestURL() {
        return super.getRequestURL(); // todo fix
    }

    @Override
    public String getPathTranslated() {
        return super.getPathTranslated(); // todo fix
    }

    @Override
    public String getServletPath() {
        return (String) getRequest().getAttribute(INCLUDE_SERVLET_PATH);
    }

    private static class ServletPartInputStream extends ServletInputStream {

        private final InputStream inputStream;

        public ServletPartInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

    }

}
