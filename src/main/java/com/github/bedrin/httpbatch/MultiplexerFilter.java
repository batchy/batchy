package com.github.bedrin.httpbatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MultiplexerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
        } else {

            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            final String contentType = request.getContentType();

            if (null != contentType && contentType.startsWith("multipart/mixed; boundary=")) {

                final String boundary = contentType.substring("multipart/mixed; boundary=".length());
                final String contentEncoding = httpServletRequest.getCharacterEncoding();

                try {
                    final PartServletRequest partServletRequest = new PartServletRequest(httpServletRequest);
                    partServletRequest.setMethod("GET");
                    partServletRequest.setContentType(null);

                    CompletableFuture.runAsync(() -> {
                        try {
                            request.getRequestDispatcher("/testServlet/1").include(partServletRequest, response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).get();

                    partServletRequest.setMethod("POST");
                    partServletRequest.setContentType("application/javascript");

                    CompletableFuture.runAsync(() -> {
                        try {
                            request.getRequestDispatcher("/testServlet/1").include(partServletRequest, response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new ServletException(e);
                }

            } else {
                chain.doFilter(request, response);
            }
        }

    }

    @Override
    public void destroy() {

    }
}
