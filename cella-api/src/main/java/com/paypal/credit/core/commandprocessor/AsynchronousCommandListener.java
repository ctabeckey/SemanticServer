package com.paypal.credit.core.commandprocessor;

/**
 *
 */
public interface AsynchronousCommandListener<C extends Command, R>
{
    /**
     * Called when a Command has completed normally and the result is available.
     * @param command the commandprovider that was executed
     * @param result the result of the commandprovider
     */
	public void commandComplete(C command, R result);

    /**
     * Called when a Command has completed abnormally.
     * @param command the commandprovider that was executed
     * @param throwable the Throwable that caused the commandprovider to complete abnormally
     */
	public void commandFailed(C command, Throwable throwable);
}
