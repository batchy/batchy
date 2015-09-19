package com.github.bedrin.batchy.io;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface HttpRequestProcessor {

    void processHttpRequest(
            Map<String, String> messageHeaders,
            String requestLine,
            Map<String, String> httpHeaders,
            InputStream inputStream) throws ServletException, IOException;

}
