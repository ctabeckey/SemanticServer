package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/8/16.
 */
public class CircularReferenceException extends ContextInitializationException {
    private static String createMessage(final String identifier, final String className) {
        return String.format("Circular reference detected when resolving id:%s of type: %s", identifier, className);
    }

    /**
     *
     * @param identifier
     * @param className
     */
    public CircularReferenceException(final String identifier, final String className) {
        super(createMessage(identifier, className));
    }
}
