package com.paypal.credit.core.processorbridge;

/**
 * Created by cbeckey on 12/8/15.
 */
public interface AsynchronousCommandCallback<R> {
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
