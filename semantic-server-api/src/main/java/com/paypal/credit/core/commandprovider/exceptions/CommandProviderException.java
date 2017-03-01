package com.paypal.credit.core.commandprovider.exceptions;

import java.security.PrivilegedActionException;

/**
 * Created by cbeckey on 11/17/15.
 */
public abstract class CommandProviderException extends Exception {
    public CommandProviderException(final String message) {
        super(message);
    }

    public CommandProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CommandProviderException(final Throwable cause) {
        super(cause);
    }

    public CommandProviderException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
