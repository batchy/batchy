package com.github.bedrin.batchy.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

public class PrefetchInputStreamTest {

    @Test
    public void testPrefetchAll() throws Exception {

        byte[] data = new byte[10];
        Arrays.fill(data, (byte) 1);

        PrefetchInputStream pis = new PrefetchInputStream(new ByteArrayInputStream(data), 20);

        assertTrue(pis.prefetch());

    }

    @Test
    public void testPrefetchMaximum() throws Exception {

        byte[] data = new byte[10];
        Arrays.fill(data, (byte) 1);

        PrefetchInputStream pis = new PrefetchInputStream(new ByteArrayInputStream(data), 11);

        assertTrue(pis.prefetch());

    }

    @Test
    public void testPrefetchPart() throws Exception {

        byte[] data = new byte[10];
        Arrays.fill(data, (byte) 1);

        PrefetchInputStream pis = new PrefetchInputStream(new ByteArrayInputStream(data), 5);

        assertFalse(pis.prefetch());

    }

    @Test
    public void testPrefetchExact() throws Exception {

        byte[] data = new byte[10];
        Arrays.fill(data, (byte) 1);

        PrefetchInputStream pis = new PrefetchInputStream(new ByteArrayInputStream(data), 10);

        assertFalse(pis.prefetch());

    }

}