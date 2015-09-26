package com.github.bedrin.batchy;

import com.github.bedrin.batchy.mux.AsyncDemultiplexer;
import com.github.bedrin.batchy.util.IoUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class BatchyServlet extends HttpServlet {

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {

        String method = request.getMethod();

        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String contentType = request.getContentType();

        if (null == contentType || !contentType.startsWith("multipart/mixed; boundary=")) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        if ("100-continue".equalsIgnoreCase(request.getHeader("Expect"))) {
            // todo optionally send HTTP 100 status code
        }

        String boundary = IoUtils.extractSemicolonSeparatedAttribute(contentType, "boundary");

        AsyncDemultiplexer demultiplexer = new AsyncDemultiplexer(request, response, boundary);
        demultiplexer.service();

    }

}
