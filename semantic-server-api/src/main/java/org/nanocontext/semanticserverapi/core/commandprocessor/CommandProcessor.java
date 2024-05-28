package org.nanocontext.semanticserverapi.core.commandprocessor;

import org.nanocontext.semanticserverapi.core.Application;

import java.util.concurrent.Callable;

/**
 * This interface is the definition of the VIX core API.  All applicationbridge projects operate through
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
	 * @param applicationImpl the "owning" application
	 */
	void setApplication(Application applicationImpl);

	/**
	 * Submit a commandprovider for synchronous execution.
	 *
	 * @param command the commandprovider to execute
	 * @return the result of the commandprovider
	 * @throws Exception if any exception occurs in executing the commandprovider
	 */
	<R> R doSynchronously(final Callable<R> command) throws Throwable;

	/**
	 * Submit a commandprovider for asynchronous execution.
	 *
	 * @param command  the commandprovider to execute
	 * @param callback an optional callback class
	 * @param <R>      the result type of the command
	 */
	<R> void doAsynchronously(final Callable<R> command, AsynchronousExecutionCallback<R> callback);

	void shutdown();

	boolean isShutdown();
}