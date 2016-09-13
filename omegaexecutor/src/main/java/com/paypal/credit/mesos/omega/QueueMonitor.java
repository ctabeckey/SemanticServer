package com.paypal.credit.mesos.omega;

/**
 * Created by cbeckey on 3/18/16.
 */
public interface QueueMonitor {

    int getCurrentConsumerCount();

    float getMaximumEnqueueTime();

    int getCurrentQueueSize();
}
