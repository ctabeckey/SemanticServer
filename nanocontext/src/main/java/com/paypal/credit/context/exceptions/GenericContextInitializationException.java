package com.paypal.credit.context.exceptions;

/**
 * A ContextIntializationException that is thrown when something really unexpected happens,
 * usually a coding issue.
 */
public class GenericContextInitializationException extends ContextInitializationException {
    public GenericContextInitializationException() {
    }

    public GenericContextInitializationException(final String message) {
        super(message);
    }

    public GenericContextInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GenericContextInitializationException(final Throwable cause) {
        super(cause);
    }
}
