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

}
