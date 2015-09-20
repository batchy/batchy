package com.github.bedrin.batchy.wrapper;

import com.github.bedrin.batchy.util.DateUtils;
import com.github.bedrin.batchy.util.IteratorEnumeration;
import com.github.bedrin.batchy.util.MultiHashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Enumeration;
import java.util.Map;

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

    // HTTP Method an dProtocol

    private String method;
    private String protocol;

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    // Input Stream related methods

    private InputStream inputStream;

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

    // Requst URI related methods

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

    // Parameters

    // todo implement multipar/form-data parameters

    private MultiHashMap<String,String> parameters;

    public void setParameters(MultiHashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getParameter(String name) {
        return parameters.getLast(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters.asArrayMap();
    }

    @Override
    public Enumeration getParameterNames() {
        return new IteratorEnumeration<String>(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.getArray(name);
    }

    // Headers

    private MultiHashMap<String,String> headers;

    public void setHeaders(MultiHashMap<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public long getDateHeader(String name) {
        final String header = getHeader(name);
        if (null == header) return -1;
        try {
            return DateUtils.parseDate(header).getTime();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public int getIntHeader(String name) {
        return Integer.parseInt(getHeader(name));
    }

    @Override
    public String getHeader(String name) {
        return headers.getFirst(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return new IteratorEnumeration<String>(headers.get(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new IteratorEnumeration<String>(headers.keySet());
    }

    // parsed headers

    @Override
    public String getCharacterEncoding() {
        return super.getCharacterEncoding(); // todo implement
    }

    @Override
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        super.setCharacterEncoding(enc); // todo implement
    }

    @Override
    public int getContentLength() {
        return super.getContentLength(); // todo implement
    }

    @Override
    public String getContentType() {
        return super.getContentType(); // todo implement
    }

    // cookies

    // todo implement cookies

    // attributes

    // todo implement attributes

}
