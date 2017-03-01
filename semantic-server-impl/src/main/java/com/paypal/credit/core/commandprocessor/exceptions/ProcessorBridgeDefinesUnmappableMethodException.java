package com.paypal.credit.core.commandprocessor.exceptions;

import com.paypal.credit.core.applicationbridge.CommandMapping;

import java.lang.reflect.Method;

/**
 *
 */
public class ProcessorBridgeDefinesUnmappableMethodException
extends ProcessorBridgeInstantiationException
{
    /** The message when only a method is given */
	private final static String METHOD_ONLY_MSG = "The method '%s' cannot be mapped to a commandprovider.";
    /** The message when only a method and CommandMapping is given */
    private final static String METHOD_AND_ANNOTATION_ONLY_MSG = "The method '%s' cannot be mapped to the commandprovider '%s' that is specified in the CommandMapping annotation.";

    /**
     * Build a useful message with just the method instance
     * @param method the method that was being called that initiated the exception
     * @return a useful message
     */
	private static String buildMessage(Method method)
	{
        return String.format(METHOD_ONLY_MSG,
                method == null ? "<unknown>" : method.getName());
	}

    /**
     * Build a useful message with the method instance and a CommandMapping
     * @param method the method that was being called that initiated the exception
     * @param commandMapping the CommandMapping annotation that was on the method that was being called ...
     * @return a useful message
     */
    private static String buildMessage(Method method, CommandMapping commandMapping)
    {
        return String.format(METHOD_AND_ANNOTATION_ONLY_MSG,
                method == null ? "<unknown>" : method.getName(),
                commandMapping == null ? "<unknown>" : commandMapping.value().getName()
                );
    }

    /**
     * Create a instance with just the method being called
     *
     * @param method the method that was being called
     */
	public ProcessorBridgeDefinesUnmappableMethodException(final Method method)
	{
		super( buildMessage(method) );
	}

    /**
     * Create a instance with just the method being called and the CommandMapping on the method
     *
     * @param method the method that was being called
     * @param commandMapping the CommandMapping on the method
     */
    public ProcessorBridgeDefinesUnmappableMethodException(final Method method, final CommandMapping commandMapping)
    {
        super( buildMessage(method, commandMapping) );
    }
}
