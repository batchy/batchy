package com.github.bedrin.batchy.mux;

import com.github.bedrin.batchy.io.HttpRequestProcessor;
import com.github.bedrin.batchy.io.MultipartParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Multiplexer implements HttpRequestProcessor {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public Multiplexer(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void processHttpRequest(
            Map<String, String> messageHeaders,
            String requestLine,
            Map<String, String> httpHeaders,
            InputStream inputStream) {

        System.out.println(requestLine);

    }

    public void service() throws IOException, ServletException {

        String contentType = request.getContentType();
        String boundary = contentType.substring("multipart/mixed; boundary=".length());
        MultipartParser multipartParser = new MultipartParser(boundary, this);
        multipartParser.parseMultipartRequest(request.getInputStream());



    }

}
