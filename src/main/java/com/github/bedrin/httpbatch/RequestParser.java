package com.github.bedrin.httpbatch;

import java.io.IOException;
import java.io.InputStream;

public class RequestParser {

    private final String boundary;

    public RequestParser(String boundary) {
        this.boundary = boundary;
    }

    public void parseRequest(InputStream inputStream) throws IOException {



    }

}
