package org.nanocontext.semanticserverapi.core.commandprocessor;

/**
 * The required definition of callback class used for
 * receiving asynchronous command execution results.
 */
public interface AsynchronousExecutionCallback<R> {
    /**
     * Called when the associated command completed successfully.
     * @param result the result of the Command (may be null)
     */
    void success(R result);

    /**
     * Called when the associated Command has thrown an exception.
     * @param t the exception thrown (will never be null)
     */
    void failure(Throwable t);
}
