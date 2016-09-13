package com.paypal.credit.mesos.omega;

import org.apache.activemq.ActiveMQQueueSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class will poll an ActiveMQ broker for the status of a
 * given queue.
 */
public class ActiveMQPollingMonitor
        implements Runnable, QueueMonitor {
    private static final long DEFAULT_POLLING_TIME = 1000l;
    private static final long DEFAULT_MAXIMUM_WAIT_TIME = 5000l;

    // Severe error handling parameters, if the initialization logic is invoked
    // more than 3 times in a 10 second period, just give up and exit.
    private static final int MAXIMUM_INITIALIZATION_RETRIES = 3;
    private static final long INITIALIZATION_RETRIES_PERIOD = 10000L;

    private final Logger logger;
    private final QueueSession queueSession;
    private final String queueName;
    private ActiveMQQueueSession session;
    private AtomicBoolean kill = new AtomicBoolean(false);
    private AtomicLong pollingTime = new AtomicLong(DEFAULT_POLLING_TIME);
    private AtomicLong maximumWaitTime = new AtomicLong(DEFAULT_MAXIMUM_WAIT_TIME);

    private AtomicBoolean statisticsAvailable = new AtomicBoolean(false);

    /**
     *
     * @param queueSession
     */
    public ActiveMQPollingMonitor(final QueueSession queueSession, final String queueName) {
        // create a Logger specific to this instance and identified by the class and queue names
        this.logger = LoggerFactory.getLogger(String.format("%s.%s", this.getClass().getName(), queueName));
        if (queueSession == null) {
            throw new IllegalArgumentException("'queueSession' is null and must not be");
        }
        if (queueName == null || queueName.trim().length() == 0) {
            throw new IllegalArgumentException("'queueName' is null or empty and must not be");
        }

        this.queueSession = queueSession;
        this.queueName = queueName;

        this.session = new ActiveMQQueueSession(this.queueSession);
    }

    /**
     * Try to recover the queue session.
     * Should be used only when we detect something like no messages or errors on send/receive
     */
    private void recoverSession() {
        try {
            this.queueSession.recover();
            this.session = new ActiveMQQueueSession(this.queueSession);
        } catch (JMSException jmsX) {
            getLogger().error("Unable to recover the queue session.", jmsX);
        }
    }

    public ActiveMQQueueSession getSession() {
        return session;
    }

    public String getQueueName() {
        return queueName;
    }

    public Logger getLogger() {
        return logger;
    }

    /** The 'magic' queue name which will cause ActiveMQ to produce stats */
    public String getStatisticsQueueName() {
        return "ActiveMQ.Statistics.Destination." + getQueueName();
    }

    public void kill() {
        kill.set(true);
    }

    public long getPollingTime() {
        return this.pollingTime.get();
    }

    public void setPollingTime(final long pollingTime) {
        if (pollingTime >= 0) {
            this.pollingTime.set(pollingTime);
        }
    }

    public long getMaximumWaitTime() {
        return this.maximumWaitTime.get();
    }

    public void setMaximumWaitTime(final long time) {
        if (time >= 0 && time > getPollingTime()) {
            this.maximumWaitTime.set(time);
        }
    }

    // ==========================================================================================
    // QueueMonitor Implementation
    // ==========================================================================================

    @Override
    public float getMaximumEnqueueTime() {
        return getMaxEnqueueTime().floatValue();
    }

    @Override
    public int getCurrentConsumerCount() {
        return getConsumerCount().intValue();
    }

    @Override
    public int getCurrentQueueSize() {
        return getSize().intValue();
    }

    // ==========================================================================================
    // Runnable Implementation
    // ==========================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        MessageConsumer consumer = null;
        MessageProducer producer = null;
        Queue replyTo = null;
        Queue statisticsDestinationQueue = null;

        // severe error handling
        long lastInitializationTime = 0L;
        int initializationRetries = 0;

        try {
            // This is a retry loop for really bad queue issues.
            // In normal operation, we iterate this loop once and exit when the
            // app is about to exit.
            // If a serious queue connection is detected during operation then we break from the
            // inner (normal processing loop) and rerun the initialization code in this (outer) loop.
            while (! this.kill.get()) {
                // if the initialization is within the INITIALIZATION_RETRIES_PERIOD
                if (lastInitializationTime > (System.currentTimeMillis() - INITIALIZATION_RETRIES_PERIOD)) {
                    if (initializationRetries > MAXIMUM_INITIALIZATION_RETRIES) {
                        // seriously SOL, just give up
                        getLogger().error(
                                String.format("Failed to initialize after %d retries within %d milliseconds, giving up.", MAXIMUM_INITIALIZATION_RETRIES, INITIALIZATION_RETRIES_PERIOD)
                        );
                        break;
                    }
                    ++initializationRetries;
                } else {
                    // note, reset this only if it is outside the INITIALIZATION_RETRIES_PERIOD
                    lastInitializationTime = System.currentTimeMillis();
                    initializationRetries = 0;
                }

                statisticsDestinationQueue = getSession().createQueue(getStatisticsQueueName());
                replyTo = getSession().createTemporaryQueue();

                consumer = getSession().createConsumer(replyTo);
                producer = getSession().createProducer(statisticsDestinationQueue);

                // Used to record the last time the iteration and to indicate the first iteration
                // when special processing is required.
                long lastIterationCompletionTime = 0L;

                // iterate until the kill flag is set
                // this is the "normal" processing loop
                while (!this.kill.get()) {
                    // Get the remaining polling time, assure that it is between 0 and getPollingTime()
                    // Do not wait on the first iteration.
                    long remainingPollTime = lastIterationCompletionTime == 0L ? 0L :
                            Math.min(getPollingTime(), Math.max(
                                    0l, getPollingTime() -
                                            (System.currentTimeMillis() - lastIterationCompletionTime)
                            ));
                    // if the remainingPollTime is greater than 0 then sleep for the remaining time
                    if (remainingPollTime > 0l) {
                        try {
                            Thread.sleep(remainingPollTime);
                        } catch (InterruptedException iX) {
                        }
                    }

                    try {
                        // remove all messages from the response queue, preserving the last one
                        // the first time through this loop, lastIterationCompletionTime will be zero
                        MapMessage mostRecentMessage = null;       // save the most recent stale message
                        if (lastIterationCompletionTime != 0L) {
                            // limit how long to wait for a message, this is NOT the polling time
                            // it is just to assure that this loop will exit sometime if no messages
                            // become available. Particularly if the broker goes offline and then comes back
                            // we need to exit this loop and re-send the statistics request message.
                            long startWaitForMessage = System.currentTimeMillis();

                            for (mostRecentMessage = (MapMessage) consumer.receiveNoWait();
                                 mostRecentMessage != null
                                         && (System.currentTimeMillis() - startWaitForMessage) < getMaximumWaitTime();
                                 mostRecentMessage = (MapMessage) consumer.receiveNoWait()
                            );

                            // timed out waiting for a message, something bad is happening
                            // recover the underlying session
                            // break from the normal processing loop (and re-initialize)
                            if (mostRecentMessage == null) {
                                // artificially create a JMSException to force error recovery logic
                                throw new JMSException(
                                        String.format("No statistics replies received in %d milliseconds, invoking recovery logic", getMaximumWaitTime())
                                );
                            }
                        }

                        if (mostRecentMessage != null) {
                            updateStatistics(mostRecentMessage);
                        }
                    } catch (JMSException e) {
                        getLogger().error("Failed to read statistics message.", e);
                        recoverSession();
                        break;
                    }

                    try {
                        // create an empty message with a replyTo header
                        Message msg = getSession().createMessage();
                        msg.setJMSReplyTo(replyTo);

                        // send the empty message
                        // get the response, waiting up to the remaining poll time
                        producer.send(msg);
                    } catch (JMSException e) {
                        getLogger().error("Failed to send request for queue statistics", e);
                        recoverSession();
                        break;
                    }

                    lastIterationCompletionTime = System.currentTimeMillis();
                    statisticsAvailable.set(true);
                }
            }
        } catch (JMSException e) {
            getLogger().error("Failed to initialize getting queue statistics.", e);
        } finally {
            try {producer.close();} catch(Throwable t){}        // eat any secondary exceptions
            try {consumer.close();} catch(Throwable t){}        // eat any secondary exceptions
        }
        statisticsAvailable.set(false);
        getLogger().info("Exiting, statistics will not be updated.");
    }

    // ================================================================================================
    // Get the latest queue statistics
    // ================================================================================================

    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getVM() {
        return getString("vm");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getSSL() {
        return getString("ssl");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getStomp() {
        return getString("stomp");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getStompSSL() {
        return getString("stomp+ssl");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getOpenwire() {
        return getString("openwire");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getBrokerId() {
        return getString("brokerId");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getBrokerName() {
        return getString("brokerName");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public String getDataDirectory() {
        return getString("dataDirectory");
    }

    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Long getMemoryUsage() {
        return getLong("memoryUsage");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Long getStoreUsage() {
        return getLong("storeUsage");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Long getTempLimit() {
        return getLong("tempLimit");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Long getTempUsage() {
        return getLong("tempUsage");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Long getStoreLimit() {
        return getLong("storeLimit");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Long getMemoryLimit() {
        return getLong("memoryLimit");
    }

    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getTempPercentUsage() {
        return getInteger("tempPercentUsage");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getStorePercentUsage() {
        return getInteger("storePercentUsage");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getMemoryPercentUsage() {
        return getInteger("memoryPercentUsage");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getConsumerCount() {
        return getInteger("consumerCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getProducerCount() {
        return getInteger("producerCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getExpiredCount() {
        return getInteger("expiredCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getDispatchCount() {
        return getInteger("dispatchCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getEnqueueCount() {
        return getInteger("enqueueCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getDequeueCount() {
        return getInteger("dequeueCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getInflightCount() {
        return getInteger("inflightCount");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getMessagesCached() {
        return getInteger("messagesCached");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Integer getSize() {
        return getInteger("size");
    }

    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Float getMinEnqueueTime() {
        return getFloat("minEnqueueTime");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Float getMaxEnqueueTime() {
        return getFloat("maxEnqueueTime");
    }
    /**
     * @throws IllegalStateException - if statistics have not been collected
     * or an unrecoverable error in statistics gathering has occurred.
     * @return
     */
    public Float getAverageEnqueueTime() {
        return getFloat("averageEnqueueTime");
    }

    // ================================================================================================
    // Private Instance Members
    // ================================================================================================

    private ReentrantReadWriteLock statisticsLock = new ReentrantReadWriteLock();
    private MapMessage mostRecentStatistics = null;

    private void updateStatistics(final MapMessage reply) {
        statisticsLock.writeLock().lock();
        try {
            this.mostRecentStatistics = reply;
        } finally {
            statisticsLock.writeLock().unlock();
        }
    }

    private String getString(final String key) {
        if (! statisticsAvailable.get()) {
            throw new IllegalStateException("Statistics are not being collected, probably due to an error.");
        }
        statisticsLock.readLock().lock();
        try {
            return this.mostRecentStatistics.getString(key);
        } catch (JMSException e) {
            getLogger().error("Error retrieving (String)" + key, e);
            return null;
        } finally {
            statisticsLock.readLock().unlock();
        }
    }

    private Long getLong(final String key) {
        if (! statisticsAvailable.get()) {
            throw new IllegalStateException("Statistics are not being collected, probably due to an error.");
        }
        statisticsLock.readLock().lock();
        try {
            return this.mostRecentStatistics.getLong(key);
        } catch (JMSException e) {
            getLogger().error("Error retrieving (Long)" + key, e);
            return null;
        } finally {
            statisticsLock.readLock().unlock();
        }
    }

    private Integer getInteger(final String key) {
        if (! statisticsAvailable.get()) {
            throw new IllegalStateException("Statistics are not being collected, probably due to an error.");
        }
        statisticsLock.readLock().lock();
        try {
            return this.mostRecentStatistics.getInt(key);
        } catch (JMSException e) {
            getLogger().error("Error retrieving (Int)" + key, e);
            return null;
        } finally {
            statisticsLock.readLock().unlock();
        }
    }

    private Float getFloat(final String key) {
        if (! statisticsAvailable.get()) {
            throw new IllegalStateException("Statistics are not being collected, probably due to an error.");
        }
        statisticsLock.readLock().lock();
        try {
            return this.mostRecentStatistics.getFloat(key);
        } catch (JMSException e) {
            getLogger().error("Error retrieving (Float)" + key, e);
            return null;
        } finally {
            statisticsLock.readLock().unlock();
        }
    }


    public static void main(String[] argv) {
        if (argv.length < 1) {
            System.err.println("No values provided.");
            System.exit(-1);
        }

        int[] val = new int[argv.length];
        try {
            for (int index = 0; index < val.length; ++index) {
                val[index] = Integer.parseInt(argv[index]);
                System.out.println(String.format("val = %d", val[index]));
            }
        } catch (NumberFormatException nfx) {
            for (int index = 0; index < val.length; ++index) {
                System.out.println(String.format("invalid value %d", val[index]));
            }
        }
    }
}
