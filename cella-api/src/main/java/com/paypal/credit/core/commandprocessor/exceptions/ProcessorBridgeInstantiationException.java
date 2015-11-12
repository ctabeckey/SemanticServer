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

}
