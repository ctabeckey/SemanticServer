package com.paypal.credit.core.commandprocessor.exceptions;

/**
 *
 * The superclass of all exceptions thrown when instantiating a 
 * commandprocessor (or processorbridge-specific commandprocessor).
 *
 */
public class ProcessorBridgeInstantiationException
extends Exception 
{
	private static final long serialVersionUID = 1L;
	private static String createMessage(final Class<?> processorBridge) {
		return String.format("Failed to create a realization of %s", processorBridge == null ? "<null>" : processorBridge.getName());
	}

	/**
	 * 
	 */
	public ProcessorBridgeInstantiationException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public ProcessorBridgeInstantiationException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ProcessorBridgeInstantiationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProcessorBridgeInstantiationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 *
	 * @param processorBridge
	 * @param rootCause
     */
	public ProcessorBridgeInstantiationException(final Class<?> processorBridge, final Throwable rootCause) {
		super(createMessage(processorBridge), rootCause);
	}
}
