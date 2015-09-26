package com.github.bedrin.batchy.wrapper;

import org.junit.Test;

import static com.github.bedrin.batchy.wrapper.PartServletRequest.parseCharacterEncoding;
import static org.junit.Assert.*;

/**
 * Created by bedrin on 26.09.2015.
 */
public class PartServletRequestTest {

    @Test
    public void testParseCharacterEncoding() throws Exception {
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data; charset=UTF-8; boundary=foo"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data; charset=UTF-8;"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data; charset=UTF-8"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data; boundary=foo; charset=UTF-8;"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data; boundary=foo; charset=UTF-8"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data;charset=UTF-8;boundary=foo"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data;boundary=foo;charset=UTF-8;"));
        assertEquals("UTF-8", parseCharacterEncoding("multipart/form-data;boundary=foo;charset=UTF-8"));
    }

}