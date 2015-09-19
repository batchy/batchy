package com.github.bedrin.httpbatch;

import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Created by bedrin on 19.09.2015.
 */
public class MultipartParserTest {

    @Test
    public void testParseMultipartRequest() throws Exception {

        String raw = "POST /batch HTTP/1.1\n" +
                "Authorization: Bearer your_auth_token\n" +
                "Host: www.googleapis.com\n" +
                "Content-Type: multipart/mixed; boundary=batch_foobarbaz\n" +
                "Content-Length: total_content_length\n" +
                "\n" +
                "--batch_foobarbaz\n" +
                "Content-Type: application/http\n" +
                "Content-ID: <item1:12930812@barnyard.example.com>\n" +
                "\n" +
                "GET /farm/v1/animals/pony\n" +
                "\n" +
                "--batch_foobarbaz\n" +
                "Content-Type: application/http\n" +
                "Content-ID: <item2:12930812@barnyard.example.com>\n" +
                "\n" +
                "PUT /farm/v1/animals/sheep\n" +
                "Content-Type: application/json\n" +
                "Content-Length: part_content_length\n" +
                "If-Match: \"etag/sheep\"\n" +
                "\n" +
                "{\n" +
                "  \"animalName\": \"sheep\",\n" +
                "  \"animalAge\": \"5\"\n" +
                "  \"peltColor\": \"green\",\n" +
                "}\n" +
                "\n" +
                "--batch_foobarbaz\n" +
                "Content-Type: application/http\n" +
                "Content-ID: <item3:12930812@barnyard.example.com>\n" +
                "\n" +
                "GET /farm/v1/animals\n" +
                "If-None-Match: \"etag/animals\"\n" +
                "\n" +
                "--batch_foobarbaz--";

        MultipartParser hrp = new MultipartParser("batch_foobarbaz");
        hrp.parseMultipartRequest(new ByteArrayInputStream(raw.getBytes()));
    }

}