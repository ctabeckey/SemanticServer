package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/8/16.
 */
public class UnknownCollectionTypeException extends ContextInitializationException {
    private static String createMessage(Class<?> parameterType) {
        return String.format("Unable to create a collection of type %s", parameterType.toString());
    }

    public UnknownCollectionTypeException(final Class<?> parameterType) {
        super(createMessage(parameterType));
    }
}
