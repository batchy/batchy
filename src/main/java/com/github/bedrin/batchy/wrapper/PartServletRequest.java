package com.github.bedrin.batchy.wrapper;

import com.github.bedrin.batchy.util.DateUtils;
import com.github.bedrin.batchy.util.IoUtils;
import com.github.bedrin.batchy.util.IteratorEnumeration;
import com.github.bedrin.batchy.util.MultiHashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Enumeration;
import java.util.Map;

import static com.github.bedrin.batchy.util.IoUtils.UTF8;

// todo implement rest of the methods
public class PartServletRequest extends HttpServletRequestWrapper {

    private final static String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    private final static String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    private final static String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
    private final static String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    private final static String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

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

    private BufferedReader servletReader;
    private ServletInputStream servletInputStream;

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (null == servletInputStream) {
            if (null != servletReader) {
                throw new IllegalStateException("Cannot call getInputStream() after getReader()");
            }
            servletInputStream = new ServletPartInputStream(inputStream);
        }
        return servletInputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (null == servletReader) {
            if (null != servletInputStream) {
                throw new IllegalStateException("Cannot call getReader() after getInputStream()");
            }
            servletReader = new BufferedReader(
                    new InputStreamReader(inputStream, null == characterEncoding ? UTF8 : getCharacterEncoding())
            );
        }
        return servletReader;
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

    // todo implement multipart/form-data parameters

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

    // todo filter and merge headers with enclosing request

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

    private String characterEncoding;

    @Override
    public String getCharacterEncoding() {
        return null == characterEncoding ? parseCharacterEncoding(getContentType()) : characterEncoding;
    }

    public static String parseCharacterEncoding(String contentType) {
        if (null != contentType) {
            int charsetIx = contentType.indexOf("charset=");
            if (charsetIx != -1) {
                int semicolonIx = contentType.indexOf(';', charsetIx);
                return contentType.substring(charsetIx + "charsest=".length() - 1, -1 == semicolonIx ? contentType.length() : semicolonIx);
            }
        }
        return null;
    }

    @Override
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        this.characterEncoding = enc;
    }

    @Override
    public int getContentLength() {
        try {
            return getIntHeader(CONTENT_LENGTH);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String getContentType() {
        return getHeader(CONTENT_TYPE);
    }

    // cookies

    // todo implement cookies

    // attributes

    private Map<String, Object> attributesMap;

    public void setAttributesMap(Map<String, Object> attributesMap) {
        this.attributesMap = attributesMap;
    }

    @Override
    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return new IteratorEnumeration<String>(attributesMap.keySet());
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributesMap.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        attributesMap.remove(name);
    }
}
