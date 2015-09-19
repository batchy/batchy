package com.github.bedrin.batchy.io;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by bedrin on 19.09.2015.
 */
public class MultipartParserTest {

    @Test
    public void testParseMultipartRequest() throws Exception {

        String raw = "--batch_foobarbaz\r\n" +
                "Content-Type: application/http\r\n" +
                "Content-ID: <item1:12930812@barnyard.example.com>\r\n" +
                "\r\n" +
                "GET /farm/v1/animals/pony\r\n" +
                "\r\n" +
                "--batch_foobarbaz\r\n" +
                "Content-Type: application/http\r\n" +
                "Content-ID: <item2:12930812@barnyard.example.com>\r\n" +
                "\r\n" +
                "PUT /farm/v1/animals/sheep\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: part_content_length\r\n" +
                "If-Match: \"etag/sheep\"\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"animalName\": \"sheep\",\r\n" +
                "  \"animalAge\": \"5\"\r\n" +
                "  \"peltColor\": \"green\"\r\n" +
                "}\r\n" +
                "\r\n" +
                "--batch_foobarbaz\r\n" +
                "Content-Type: application/http\r\n" +
                "Content-ID: <item3:12930812@barnyard.example.com>\r\n" +
                "\r\n" +
                "GET /farm/v1/animals\r\n" +
                "If-None-Match: \"etag/animals\"\r\n" +
                "\r\n" +
                "--batch_foobarbaz--";

        HttpRequestProcessor httpRequestProcessor = mock(HttpRequestProcessor.class);
        MultipartParser hrp = new MultipartParser("batch_foobarbaz", httpRequestProcessor);
        hrp.parseMultipartRequest(new ByteArrayInputStream(raw.getBytes()));

        verify(httpRequestProcessor, times(3)).processHttpRequest(anyMap(), anyString(), anyMap(), Matchers.<InputStream>anyObject());
    }

}