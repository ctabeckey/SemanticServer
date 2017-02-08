package com.paypal.credit.context;

/**
 * Created by cbeckey on 1/23/17.
 */
public interface ActiveBean extends Runnable {
    /**
     * An ActiveBean must implement Runnable and must implement a
     * way to shut it down when sgutdown() is called.
     */
    void shutdown();
}
