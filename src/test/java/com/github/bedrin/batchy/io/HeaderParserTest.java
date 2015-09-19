package com.github.bedrin.batchy.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class HeaderParserTest {

    @Test
    public void testGet() throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream((
                "GET /farm/v1/animals\r\n" +
                "If-None-Match: \"etag/animals\"\r\n" +
                "\r\n"
        ).getBytes());

        HeaderParser headerParser = new HeaderParser(false);

        String requestLine = headerParser.readFirstNotEmptyLine(inputStream);
        Map<String, String> headers = headerParser.parseHeader(inputStream);

        assertEquals("GET /farm/v1/animals", requestLine);
        assertEquals("\"etag/animals\"", headers.get("If-None-Match"));

    }

    @Test
    public void testPut() throws IOException {

        String body =
                "{\r\n" +
                "  \"animalName\": \"sheep\",\r\n" +
                "  \"animalAge\": \"5\"\r\n" +
                "  \"peltColor\": \"green\"\r\n" +
                "}\r\n" +
                "\r\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream((
                "PUT /farm/v1/animals/sheep\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: part_content_length\r\n" +
                        "If-Match: \"etag/sheep\"\r\n" +
                        "\r\n" +
                        body
        ).getBytes());

        HeaderParser headerParser = new HeaderParser(false);

        String requestLine = headerParser.readFirstNotEmptyLine(inputStream);
        Map<String, String> headers = headerParser.parseHeader(inputStream);

        assertEquals("PUT /farm/v1/animals/sheep", requestLine);
        assertEquals("application/json", headers.get("Content-Type"));
        assertEquals("part_content_length", headers.get("Content-Length"));
        assertEquals("\"etag/sheep\"", headers.get("If-Match"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        assertEquals(body, new String(baos.toByteArray()));

    }

}