package com.github.bedrin.httpbatch;

import com.github.bedrin.httpbatch.wrapper.PartServletRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.*;

public class MultiplexerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, FilterChain chain) throws IOException, ServletException {

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

                    final Future<?> getFuture = executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                request.getRequestDispatcher("/testServlet/1").include(partServletRequest, response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    partServletRequest.setMethod("POST");
                    partServletRequest.setContentType("application/javascript");

                    final Future<?> postFuture = executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                request.getRequestDispatcher("/testServlet/1").include(partServletRequest, response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    getFuture.get();
                    postFuture.get();
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                } catch (ExecutionException e) {
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
