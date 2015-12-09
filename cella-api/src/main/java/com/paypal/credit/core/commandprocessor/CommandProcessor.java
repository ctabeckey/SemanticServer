package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;

/**
 * This interface is the definition of the VIX core API.  All processorbridge projects operate through
 * this interface exclusively.
 * 
 * All of the methods within this interface behave as follows:
 * 1.) The return value of a successful request is a populated value object.
 * 2.) If the request did not find the requested object(s) then the return value is an empty
 * Collection or a null if the return type is not a Collection
 *
 */
public interface CommandProcessor {
	/**
	 * Set the Application in which this CommandProcessor is running.
	 * The implementation should retain this reference.
	 *
	 * @param application the "owning" application
	 */
	void setApplication(Application application);

	/**
	 * Submit a commandprovider for synchronous execution.
	 *
	 * @param command the commandprovider to execute
	 * @return the result of the commandprovider
	 * @throws Exception if any exception occurs in executing the commandprovider
	 */
	<R> R doSynchronously(final Command<R> command)
			throws Exception, Throwable;

	/**
	 * Submit a commandprovider for asynchronous execution.
	 *
	 * @param command  the commandprovider to execute
	 * @param callback an optional callback class
	 * @param <R>      the result type of the command
	 */
	<R> void doAsynchronously(final Command<R> command, AsynchronousExecutionCallback<R> callback);

	void shutdown();

	boolean isShutdown();
}