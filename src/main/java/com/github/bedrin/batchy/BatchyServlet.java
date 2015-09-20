package com.github.bedrin.batchy;

import com.github.bedrin.batchy.mux.Multiplexer;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchyServlet extends HttpServlet {

    final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {

        String method = request.getMethod();

        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)) {
            response.sendError(405);
            return;
        }

        String contentType = request.getContentType();

        if (null == contentType || !contentType.startsWith("multipart/mixed; boundary=")) {
            response.sendError(415);
            return;
        }

        /*{
            StringBuilder sb = new StringBuilder();
            int i;
            final ServletInputStream inputStream = request.getInputStream();
            while ((i = inputStream.read()) != -1) {
                sb.appendCodePoint(i);
            }
            System.out.println(sb.toString());
        }*/


        Multiplexer multiplexer = new Multiplexer(request, response);
        multiplexer.service();

        /*String boundary = contentType.substring("multipart/mixed; boundary=".length());
        String contentEncoding = request.getCharacterEncoding();

        try {

            Future<?> getFuture = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        final PartServletRequest partServletRequest = new PartServletRequest(request);
                        partServletRequest.setMethod("GET");
                        partServletRequest.setContentType(null);
                        request.getRequestDispatcher("/testServlet/1").include(partServletRequest, response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Future<?> postFuture = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        final PartServletRequest partServletRequest = new PartServletRequest(request);
                        partServletRequest.setMethod("POST");
                        partServletRequest.setContentType("application/javascript");
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
        }*/

    }

}
