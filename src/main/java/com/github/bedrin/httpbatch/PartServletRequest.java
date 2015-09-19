package com.github.bedrin.httpbatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PartServletRequest extends HttpServletRequestWrapper {

    public PartServletRequest(HttpServletRequest request) {
        super(request);
    }

    private String method;

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
