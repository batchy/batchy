package com.github.bedrin.batchy.io;

import com.github.bedrin.batchy.util.MultiHashMap;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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

        verify(httpRequestProcessor, times(3)).processHttpRequest(Matchers.<MultiHashMap<String,String>>anyObject(), anyString(), Matchers.<MultiHashMap<String,String>>anyObject(), Matchers.<InputStream>anyObject());
    }

}