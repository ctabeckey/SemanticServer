package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple abstract class to implement CellaAwareCommand
 */
public abstract class AbstractApplicationAwareCommand
            implements ApplicationAwareCommand {
    private final ReentrantLock applicationLock = new ReentrantLock();
    private Application application;

    /**
     * Provides Cella environment access to the Command implementations.
     *
     * @param application
     */
    @Override
    public void setApplicationContext(final Application application) {
        applicationLock.lock();
        try {
            this.application = application;
        } finally {
            applicationLock.unlock();
        }
    }

    protected Application getApplicationContext() {
        applicationLock.lock();
        try {
            return this.application;
        } finally {
            applicationLock.unlock();
        }
    }
}
