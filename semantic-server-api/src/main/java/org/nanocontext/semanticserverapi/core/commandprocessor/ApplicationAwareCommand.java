package org.nanocontext.semanticserverapi.core.commandprocessor;

import org.nanocontext.semanticserverapi.core.Application;

/**
 * Optional interface tat may be implemented by Commands
 * that need access to the Application context.
 */
public interface ApplicationAwareCommand {
    /**
     * Called to set the Cell Application context
     * @param application the application context
     */
    void setApplicationContext(Application application);
}
