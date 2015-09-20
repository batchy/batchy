package com.github.bedrin.batchy.mux;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Multiplexer {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String boundary;

    public Multiplexer(HttpServletRequest request, HttpServletResponse response, String boundary) {
        this.request = request;
        this.response = response;
        this.boundary = boundary;
    }

    private Lock partsLock = new ReentrantLock();
    private Condition allPartsProcessed = partsLock.newCondition();
    private int activeRequests;

    public void addActiveRequest() {
        partsLock.lock();
        try {
            activeRequests++;
        } finally {
            partsLock.unlock();
        }
    }

    public void finishActiveRequest() {
        partsLock.lock();
        try {
            if (0 == --activeRequests) {
                allPartsProcessed.signalAll();
            }
        } finally {
            partsLock.unlock();
        }
    }

    public void await() throws InterruptedException {
        partsLock.lock();
        try {
            while (activeRequests > 0) allPartsProcessed.await();
        } finally {
            partsLock.unlock();
        }
    }

}
