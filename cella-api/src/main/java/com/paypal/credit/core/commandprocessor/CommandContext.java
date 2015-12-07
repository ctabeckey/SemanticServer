package com.paypal.credit.core.commandprocessor;

import com.paypal.credit.core.commandprovider.RootCommandProvider;
import com.paypal.credit.core.datasourceprovider.RootDataSourceProviderFactory;

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
	public abstract RootDataSourceProviderFactory getProviderFactory();

	/**
	 * @return the command provider
	 */
	public abstract RootCommandProvider getCommandProvider();
	
}
