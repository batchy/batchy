package com.github.bedrin.batchy.io;

import com.github.bedrin.batchy.util.MultiHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class HeaderParser {

    private final boolean strictCRLF;

    public HeaderParser(boolean strictCRLF) {
        this.strictCRLF = strictCRLF;
    }

    public String readFirstNotEmptyLine(InputStream in) throws IOException {
        PushbackInputStream pis = new PushbackInputStream(in);
        int i;

        StringBuilder stringBuilder = new StringBuilder();

        while ((i = pis.read()) != -1) {

            boolean lineEnd = false;

            if ('\r' == i) {
                lineEnd = !strictCRLF;
                if ((i = pis.read()) != '\n') pis.unread(i);
                else if (strictCRLF) lineEnd = true;
            } else if ('\n' == i) {
                lineEnd = !strictCRLF;
            }

            if (lineEnd) {
                break;
            } else {
                stringBuilder.appendCodePoint(i);
            }

        }

        return stringBuilder.toString();
    }

    // TODO support folder headers and other special cases from RFC822 (section 3 - Lexical)
    public MultiHashMap<String, String> parseHeader(InputStream in) throws IOException {

        final MultiHashMap<String, String> headers = new MultiHashMap<String, String>();

        PushbackInputStream pis = new PushbackInputStream(in);
        int i;

        StringBuilder stringBuilder = new StringBuilder();

        boolean lineEnd = false;

        while ((i = pis.read()) != -1) {

            boolean previousLineEnd = lineEnd;
            lineEnd = false;

            if ('\r' == i) {
                lineEnd = !strictCRLF;
                if ((i = pis.read()) != '\n') pis.unread(i);
                else if (strictCRLF) lineEnd = true;
            } else if ('\n' == i) {
                lineEnd = !strictCRLF;
            }

            if (lineEnd) {
                if (stringBuilder.length() > 0) {
                    parseHeader(headers, stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
                if (previousLineEnd) break; // came to the body
            } else {
                stringBuilder.appendCodePoint(i);
            }

        }

        return headers;

    }

    private void parseHeader(MultiHashMap<String, String> headers, String header) {
        String name;
        String value = null;

        int pos = header.indexOf(':');
        if (pos > -1) {
            name = header.substring(0, pos).trim();
            if (header.length() > pos) {
                value = header.substring(pos + 1).trim();
            }
        } else {
            name = header.trim();
        }

        headers.add(name, value);
    }

}
