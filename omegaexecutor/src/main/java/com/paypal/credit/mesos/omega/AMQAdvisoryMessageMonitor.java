package com.paypal.credit.mesos.omega;

import java.net.URL;

/**
 * Created by cbeckey on 2/29/16.
 */
public class AMQAdvisoryMessageMonitor {
    private final URL[] amqBrokers;

    public AMQAdvisoryMessageMonitor(final URL[] amqBrokers) {
        this.amqBrokers = amqBrokers;
    }

    public void startMonitoringQueue(final String queueName) {

    }

    public void stopMonitoringQueue(final String queueName) {

    }

}
