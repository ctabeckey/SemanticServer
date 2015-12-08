package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;

/**
 * An interface for all Command
 *
 * The Generic types are:
 * R - the result of the Command
 */
public interface Command<R>
{
	/**
     * Provides environment access to the Command implementations.
	 * 
	 * @param application
	 */
	public void setApplicationContext(Application application);

	/**
	 * A synchronous execution of this command.
	 * Asynchronous execution is managed by the CommandProcessor, this is the
     * (only) invocation of the command.
	 * 
	 * @return
	 */
	public R invoke() throws Throwable;
	
}
