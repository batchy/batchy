package com.github.bedrin.batchy.io;

import com.github.bedrin.batchy.util.MultiHashMap;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

public interface HttpRequestProcessor {

    void processHttpRequest(
            MultiHashMap<String, String> messageHeaders,
            String requestLine,
            MultiHashMap<String, String> httpHeaders,
            InputStream inputStream) throws ServletException, IOException;

}
