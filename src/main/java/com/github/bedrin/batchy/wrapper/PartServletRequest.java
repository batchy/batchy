package com.github.bedrin.batchy.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.InputStream;
import java.util.Map;

public class PartServletRequest extends HttpServletRequestWrapper {

    public PartServletRequest(HttpServletRequest request) {
        super(request);
    }

    public static PartServletRequest build(
            HttpServletRequest parent,
            Map<String, String> messageHeaders,
            String requestLine,
            Map<String, String> httpHeaders,
            InputStream inputStream) {
        PartServletRequest request = new PartServletRequest(parent);
        return request;
    }

    private String method;
    private String contentType;

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
