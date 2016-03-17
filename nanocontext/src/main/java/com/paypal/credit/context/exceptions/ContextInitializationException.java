package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/4/16.
 */
public abstract class ContextInitializationException extends Exception {
    public ContextInitializationException() {
    }

    public ContextInitializationException(final String message) {
        super(message);
    }

    public ContextInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ContextInitializationException(final Throwable cause) {
        super(cause);
    }
}
