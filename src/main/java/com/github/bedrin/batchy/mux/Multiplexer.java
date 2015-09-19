package com.github.bedrin.batchy.mux;

import com.github.bedrin.batchy.io.HttpRequestProcessor;
import com.github.bedrin.batchy.io.MultipartParser;
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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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

        Map<String, String> params = new HashMap<String, String>();

        // Decode parameters from the URI
        int qmi = uri.indexOf('?');
        if (qmi >= 0) {
            decodeParms(uri.substring(qmi + 1), params);
            uri = decodePercent(uri.substring(0, qmi));
        } else {
            uri = decodePercent(uri);
        }

        String path = uri.substring(request.getContextPath().length());
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
        PartServletRequest servletRequest = new PartServletRequest(this.request);
        servletRequest.setMethod(method);
        servletRequest.setInputStream(inputStream);
        requestDispatcher.include(servletRequest, new PartServletResponse(response));

    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g.
     * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
     * Map. NOTE: this doesn't support multiple identical keys due to the
     * simplicity of Map.
     */
    private void decodeParms(String parms, Map<String, String> p) throws IOException {
        if (parms == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
            } else {
                p.put(decodePercent(e).trim(), "");
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

        String contentType = request.getContentType();
        String boundary = contentType.substring("multipart/mixed; boundary=".length());
        MultipartParser multipartParser = new MultipartParser(boundary, this);
        multipartParser.parseMultipartRequest(request.getInputStream());



    }

}