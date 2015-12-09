package com.paypal.credit.core.commandprocessor.exceptions;

import com.paypal.credit.core.semantics.CommandClassSemantics;

/**
 * Created by cbeckey on 11/16/15.
 */
public class UnknownCommandException extends Exception {
    private static String createMessage(Class<?> commandClass) {
        return String.format("Unknown throwable caught when executing %s", commandClass.getName());
    }

    private static String createMessage(String commandName) {
        return String.format("Unknown throwable caught when executing %s", commandName);
    }

    private static String createMessage(CommandClassSemantics ccs) {
        return String.format("Unknown throwable caught when executing %s", ccs.toString());
    }

    public UnknownCommandException(final Class<?> commandClass, final Throwable cause) {
        super(createMessage(commandClass), cause);
    }

    public UnknownCommandException(final String commandName, final Throwable cause) {
        super(createMessage(commandName), cause);
    }

    public UnknownCommandException(final CommandClassSemantics ccs, final Throwable cause) {
        super(createMessage(ccs), cause);
    }
}
