package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;

/**
 * Optional interface tat may be implemented by Commands
 * that need access to the Application context.
 */
public interface CellaAwareCommand {
    /**
     * Called to set the Cell Application context
     * @param application the application context
     */
    void setApplicationContext(Application application);
}
