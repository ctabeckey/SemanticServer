package org.nanocontext.semanticserverapi.core.commandprovider.exceptions;

/**
 * Created by cbeckey on 11/16/15.
 */
public class CommandInstantiationException extends CommandProviderException {
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
