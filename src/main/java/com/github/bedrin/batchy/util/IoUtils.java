package com.github.bedrin.batchy.util;

import java.io.IOException;
import java.io.InputStream;

public class IoUtils {

    public static final String UTF8 = "UTF-8";
    public static final String ISO_8859_1_ENCODING = "ISO-8859-1";

    public static void drainInputStream(InputStream is) throws IOException {
        byte[] buffer = new byte[8192];
        while (is.read(buffer) != -1);
    }

    public static String extractSemicolonSeparatedAttribute(String raw, String attributeName) {
        if (null != raw) {
            int charsetIx = raw.indexOf(attributeName + "=");
            if (charsetIx != -1) {
                int semicolonIx = raw.indexOf(';', charsetIx);
                return IoUtils.trim(
                        raw.substring(
                                charsetIx + (attributeName + "=").length(), -1 == semicolonIx ? raw.length() : semicolonIx
                        ).trim(),"\""
                );
            }
        }
        return null;
    }

    public static String trim(String source, String characters) {
        int st = 0;
        int len = source.length();
        char[] val = source.toCharArray();

        while ((st < len) && (characters.indexOf(val[st]) != -1)) {
            st++;
        }
        while ((st < len) && (characters.indexOf(val[len - 1]) != -1)) {
            len--;
        }
        return ((st > 0) || (len < source.length())) ? source.substring(st, len) : source;
    }

}
