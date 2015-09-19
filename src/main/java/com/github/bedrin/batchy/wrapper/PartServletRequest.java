package com.github.bedrin.batchy.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PartServletRequest extends HttpServletRequestWrapper {

    public PartServletRequest(HttpServletRequest request) {
        super(request);
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
