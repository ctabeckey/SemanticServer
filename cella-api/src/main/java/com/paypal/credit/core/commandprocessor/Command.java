package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.Application;

import java.util.concurrent.Callable;

/**
 * An interface for all Command
 *
 * The Generic types are:
 * R - the result of the Command
 */
public interface Command<R> extends Callable<R>
{
	/**
     * Provides environment access to the Command implementations.
	 * 
	 * @param application
	 */
	public void setApplicationContext(Application application);
}
