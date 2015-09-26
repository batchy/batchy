package com.github.bedrin.batchy.mux;

import com.github.bedrin.batchy.io.HttpRequestProcessor;
import com.github.bedrin.batchy.io.MultipartParser;
import com.github.bedrin.batchy.io.PrefetchInputStream;
import com.github.bedrin.batchy.util.MultiHashMap;
import com.github.bedrin.batchy.wrapper.PartServletRequest;
import com.github.bedrin.batchy.wrapper.PartServletResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncDemultiplexer implements HttpRequestProcessor {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String boundary;

    private final AsyncMultiplexer multiplexer;

    private final Map<String, Object> attributesMap = new ConcurrentHashMap<String, Object>();

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public AsyncDemultiplexer(HttpServletRequest request, HttpServletResponse response, String boundary) {
        this.request = request;
        this.response = response;
        this.boundary = boundary;
        this.multiplexer = new AsyncMultiplexer(request, response, boundary);

        final Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            Object attributeNameObj = attributeNames.nextElement();
            if (null != attributeNameObj && attributeNameObj instanceof String) {
                final String attributeName = (String) attributeNameObj;
                attributesMap.put(attributeName, request.getAttribute(attributeName));
            }
        }

    }

    @Override
    public void processHttpRequest(
            MultiHashMap<String, String> messageHeaders,
            String requestLine,
            MultiHashMap<String, String> httpHeaders,
            InputStream inputStream) throws ServletException, IOException {

        StringTokenizer st = new StringTokenizer(requestLine);
        if (!st.hasMoreTokens()) {
            throw new ServletException("BAD REQUEST: Syntax error. Usage: GET /example/file.html");
        }

        String method = st.nextToken();

        if (!st.hasMoreTokens()) {
            throw new ServletException("BAD REQUEST: Missing URI. Usage: GET /example/file.html");
        }

        String uri = st.nextToken();

        MultiHashMap<String, String> params = new MultiHashMap<String, String>();

        // Decode parameters from the URI
        int qmi = uri.indexOf('?');
        if (qmi >= 0) {
            decodeParams(uri.substring(qmi + 1), params);
            uri = decodePercent(uri.substring(0, qmi));
        } else {
            uri = decodePercent(uri);
        }

        String protocolVersion;
        if (st.hasMoreTokens()) {
            protocolVersion = st.nextToken();
        } else {
            protocolVersion = "HTTP/1.1";
        }

        PrefetchInputStream pis = new PrefetchInputStream(inputStream);

        String path = uri.substring(request.getContextPath().length());
        final RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
        final PartServletRequest servletRequest = new PartServletRequest(this.request);
        servletRequest.setMethod(method);
        servletRequest.setProtocol(protocolVersion);
        servletRequest.setInputStream(pis);
        servletRequest.setParameters(params);
        servletRequest.setHeaders(httpHeaders); // todo headers must be filtered and merged
        servletRequest.setAttributesMap(attributesMap);

        final PartServletResponse servletResponse = new PartServletResponse(multiplexer, response, boundary);

        if (pis.prefetch()) {
            multiplexer.addActiveRequest();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // todo write boundary before the actual response
                        requestDispatcher.include(servletRequest, servletResponse);
                        servletResponse.flushBuffer();
                    } catch (ServletException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            multiplexer.getResponseLock().unlock();
                        } catch (IllegalMonitorStateException e) {
                            e.printStackTrace();
                        }
                    }
                    multiplexer.finishActiveRequest();
                }
            });
        } else {
            try {
                // todo write boundary before the actual response
                requestDispatcher.include(servletRequest, servletResponse);
                servletResponse.flushBuffer();
            } finally {
                try {
                    multiplexer.getResponseLock().unlock();
                } catch (IllegalMonitorStateException e) {
                    e.printStackTrace();
                }
            }
            drainInputStream(pis); // todo should we drain the input stream if it is not read by callee ?
        }

    }

    private void drainInputStream(InputStream is) throws IOException {
        while (is.read() != -1);
    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g.
     * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
     * Map. NOTE: this doesn't support multiple identical keys due to the
     * simplicity of Map.
     */
    private void decodeParams(String parms, MultiHashMap<String, String> p) throws IOException {
        if (parms == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                p.add(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
            } else {
                p.add(decodePercent(e).trim(), "");
            }
        }
    }

    /**
     * Decode percent encoded <code>String</code> values.
     *
     * @param str
     *            the percent encoded <code>String</code>
     * @return expanded form of the input, for example "foo%20bar" becomes
     *         "foo bar"
     */
    protected static String decodePercent(String str) throws IOException {
        String decoded;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
        return decoded;
    }



    public void service() throws IOException, ServletException {

        MultipartParser multipartParser = new MultipartParser(boundary, this);
        multipartParser.parseMultipartRequest(request.getInputStream());

        try {
            multiplexer.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // todo should we do something better?
        }

        response.getOutputStream().write(("\r\n--" + boundary + "--").getBytes());

        // todo write attributes back to original request

    }

}
