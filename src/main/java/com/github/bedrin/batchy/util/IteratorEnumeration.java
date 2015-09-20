package com.github.bedrin.batchy.util;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<E> implements Enumeration<E> {

    private final Iterator<E> iterator;

    public IteratorEnumeration(Iterable<E> iterable) {
        this(iterable.iterator());
    }

    public IteratorEnumeration(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public E nextElement() {
        return iterator.next();
    }

}
