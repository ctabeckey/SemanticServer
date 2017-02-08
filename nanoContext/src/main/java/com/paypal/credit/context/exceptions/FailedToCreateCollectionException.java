package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/4/16.
 */
public class FailedToCreateCollectionException extends ContextInitializationException {
    private final static String createMessage(final Class<?> parameterType) {
        return String.format("Unable to create Collection type %s for a list parameter.");
    }

    public FailedToCreateCollectionException(final Class<?> parameterType) {
        super(createMessage(parameterType));
    }
}
