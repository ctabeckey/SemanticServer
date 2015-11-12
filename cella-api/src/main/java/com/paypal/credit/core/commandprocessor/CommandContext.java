package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.commandprovider.RootCommandProvider;
import com.paypal.credit.core.serviceprovider.RootServiceProviderFactory;

/**
 * This interface defines the context available to a Command.
 *
 */
public interface CommandContext
{

	/**
	 * @return the CommandProcessor
	 */
	public abstract CommandProcessor getCommandProcessor();

	/**
	 * @return
	 */
	public abstract RootServiceProviderFactory getProviderFactory();

	/**
	 * @return the command provider
	 */
	public abstract RootCommandProvider getCommandProvider();
	
}
