package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/8/16.
 */
public class InvalidElementTypeException extends ContextInitializationException {
    private static String createMessage(final Class<?> componentType, final Object argValue) {
        return String.format("Unable to insert element of type %s into collection of type %s", argValue.getClass(), componentType);
    }

    public InvalidElementTypeException(final Class<?> componentType, final Object argValue) {
        super(createMessage(componentType, argValue));
    }
}
