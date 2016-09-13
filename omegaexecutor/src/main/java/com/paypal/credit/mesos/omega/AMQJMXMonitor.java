package com.paypal.credit.mesos.omega;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.monitor.MonitorNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Created by cbeckey on 3/1/16.
 */
public class AMQJMXMonitor extends Thread {
    private final JMXServiceURL brokerUrl;
    private JMXConnector jmxConnector = null;
    private MBeanServerConnection mBeanServerConnection = null;

    // NOTE that this is NOT a ConcurrentHashMap because it is externally synchronized
    private final Map<ObjectName, NotificationListener> queueListeners = new HashMap<>();
    private final ReentrantReadWriteLock queueListenerMapLock = new ReentrantReadWriteLock();

    /**
     *
     * @param brokerUrl - the URL of the AMQ broker, should be something like:
     *                  "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"
     */
    public AMQJMXMonitor(final String brokerUrl)
            throws MalformedURLException {
        super(createThreadName(brokerUrl));
        this.brokerUrl = new JMXServiceURL(brokerUrl);
    }

    private static String createThreadName(String brokerUrl) {
        return String.format("AMQJMXMonitor-%s", brokerUrl);
    }

    /**
     */
    @Override
    public void run() {
        try {
            connect();
            while(true) {
                try {
                    Thread.sleep(10000l);
                    // TODO: implement heartbeat here, ping the monitored JMX Agent
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException | MalformedObjectNameException | InstanceNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                disconnect();
            } catch (IOException | ListenerNotFoundException | MalformedObjectNameException | InstanceNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void connect()
            throws IOException, MalformedObjectNameException, InstanceNotFoundException {
        AMQBrokerJMXListener jmxListener = new AMQBrokerJMXListener();

        jmxConnector = JMXConnectorFactory.connect(brokerUrl, null);
        mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        startMonitoringBroker();
    }

    private void disconnect()
            throws IOException, ListenerNotFoundException, MalformedObjectNameException, InstanceNotFoundException {
        stopMonitoringBroker();
        jmxConnector.close();
    }

    private static ObjectName createFullyQualifiedBrokerName()
            throws MalformedObjectNameException {
        return new ObjectName("org.apache.activemq.localhost.Broker");
    }

    private static ObjectName createFullyQualifiedQueueName(final String queueName)
            throws MalformedObjectNameException {
        return new ObjectName(String.format("org.apache.activemq.localhost.Queue.%s", queueName));
    }

    private static ObjectName createFullyQualifiedTopicName(final String topicName)
            throws MalformedObjectNameException {
        return new ObjectName(String.format("org.apache.activemq.localhost.Topic.%s", topicName));
    }

    public void startMonitoringBroker()
            throws MalformedObjectNameException, IOException, InstanceNotFoundException {
        ObjectName fqBrokerName = createFullyQualifiedBrokerName();
        NotificationListener listener = new AMQBrokerJMXListener();

        startMonitoringJMXObject(fqBrokerName, listener);
    }

    public void stopMonitoringBroker()
            throws MalformedObjectNameException, IOException, InstanceNotFoundException, ListenerNotFoundException {
        ObjectName fqBrokerName = createFullyQualifiedBrokerName();
        stopMonitoringJMXObject(fqBrokerName);
    }

    public void startMonitoringQueue(final String queueName)
            throws MalformedObjectNameException, IOException, InstanceNotFoundException {
        ObjectName fqQueueName = createFullyQualifiedQueueName(queueName);
        NotificationListener listener = new AMQBrokerJMXListener();

        startMonitoringJMXObject(fqQueueName, listener);
    }

    public void stopMonitoringQueue(final String queueName)
            throws MalformedObjectNameException, IOException, InstanceNotFoundException, ListenerNotFoundException {
        ObjectName fqQueueName = createFullyQualifiedQueueName(queueName);
        stopMonitoringJMXObject(fqQueueName);
    }

    public void startMonitoringTopic(final String topicName)
            throws MalformedObjectNameException, IOException, InstanceNotFoundException {
        ObjectName fqQueueName = createFullyQualifiedTopicName(topicName);
        NotificationListener listener = new AMQBrokerJMXListener();

        startMonitoringJMXObject(fqQueueName, listener);
    }

    public void stopMonitoringTopic(final String topicName)
            throws MalformedObjectNameException, IOException, InstanceNotFoundException, ListenerNotFoundException {
        ObjectName fqQueueName = createFullyQualifiedTopicName(topicName);
        stopMonitoringJMXObject(fqQueueName);
    }

    private void startMonitoringJMXObject(ObjectName objectName, NotificationListener listener)
            throws InstanceNotFoundException, IOException {
        queueListenerMapLock.writeLock().lock();
        try {
            mBeanServerConnection.addNotificationListener(objectName, listener, null, null);
            queueListeners.put(objectName, listener);
        }
        finally {
            queueListenerMapLock.writeLock().unlock();
        }
    }

    private void stopMonitoringJMXObject(ObjectName objectName)
            throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        queueListenerMapLock.writeLock().lock();
        try {
            NotificationListener listener = queueListeners.get(objectName);
            if (listener != null) {
                mBeanServerConnection.removeNotificationListener(objectName, listener);
            }
            queueListeners.remove(objectName);
        }
        finally {
            queueListenerMapLock.writeLock().unlock();
        }
    }

    /**
     *
     */
    public static class AMQBrokerJMXListener implements NotificationListener {

        /**
         *
         * @param notification
         * @param handback
         */
        public void handleNotification(Notification notification, Object handback) {
            Class<?> notificationClass = notification.getClass();
            String notificationType = notification.getType();

            if (notification instanceof AttributeChangeNotification) {

            } else if (notification instanceof MBeanServerNotification) {

            } else if (notification instanceof MonitorNotification) {

            }


        }
    }
}
