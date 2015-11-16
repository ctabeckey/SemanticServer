package com.paypal.credit.core.commandprocessor;

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
	 * @param commandContext
	 */
	public void setCommandContext(CommandContext commandContext);

	/**
	 * Make the routing token available so that the command
	 * can direct the call.
	 *
	 * @return
	 */
	public void setRoutingToken(RoutingToken routingToken);

	/**
	 * A synchronous execution of this command.
	 * Asynchronous execution is managed by the CommandProcessor, this is the
     * (only) invocation of the command.
	 * 
	 * @return
	 */
	public R invoke() throws Throwable;
	
}
