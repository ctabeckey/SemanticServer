package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 1/23/17.
 */
public class InvalidActiveClassAttributionException extends ContextInitializationException {
    private static String createMessage(final String id, final String className) {
        return String.format("Bean %s (of type %s) is marked as active but does not extend Thread or implement Runnable.");
    }

    public InvalidActiveClassAttributionException(final String id, final String className) {
        super(createMessage(id, className));
    }

    public InvalidActiveClassAttributionException(final String id, final String className, final Throwable t) {
        super(createMessage(id, className), t);
    }
}
