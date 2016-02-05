package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/4/16.
 */
public class CannotCreateObjectFromStringException extends ContextInitializationException {
    private static String createMessage(final Class<?> clazz) {
        return String.format("Unable to create an instance of %s from a String, no valueOf(String) or <ctor>(String).", clazz == null ? "<null>" : clazz.getName());
    }

    public CannotCreateObjectFromStringException(final Class<?> clazz, Exception x) {
        super(createMessage(clazz), x);
    }
}
