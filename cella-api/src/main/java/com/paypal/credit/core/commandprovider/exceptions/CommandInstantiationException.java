package com.paypal.credit.core.commandprovider.exceptions;

/**
 * Created by cbeckey on 11/16/15.
 */
public class CommandInstantiationException extends Exception {
    private static String createMessage(final String commandFactoryName) {
        return String.format("Command factory '%s' could not instantiate command.", commandFactoryName);
    }

    public CommandInstantiationException(final String commandFactoryName) {
        super(createMessage(commandFactoryName));
    }


    public CommandInstantiationException(final String commandFactoryName, final Throwable cause) {
        super(createMessage(commandFactoryName), cause);
    }
}
