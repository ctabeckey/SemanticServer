package com.paypal.credit.mesos.omega;

/**
 * Created by cbeckey on 2/29/16.
 */
public interface MessageMonitorHandler {
    /**
     *
     * @param queueName
     */
    void queueBackup(final String queueName);
}
