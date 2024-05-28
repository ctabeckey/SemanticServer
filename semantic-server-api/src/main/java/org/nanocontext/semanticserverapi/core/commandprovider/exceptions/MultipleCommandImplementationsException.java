package org.nanocontext.semanticserverapi.core.commandprovider.exceptions;

import org.nanocontext.semanticserverapi.core.semantics.CommandClassSemantics;
import org.nanocontext.semanticserverapi.core.commandprovider.CommandInstantiationToken;

import java.util.List;

/**
 * Created by cbeckey on 11/17/15.
 */
public class MultipleCommandImplementationsException extends CommandProviderException {
    private static String createMessage(
            final CommandClassSemantics commandClassSemantics,
            final List<CommandInstantiationToken> commandInstantiationTokens) {
        StringBuilder providersIdentification = new StringBuilder();
        for(CommandInstantiationToken token : commandInstantiationTokens) {
            if (providersIdentification.length() > 0) {
                providersIdentification.append(',');
            }
            providersIdentification.append(token.getCommandProvider().getPublisher());
        }

        return String.format("Multiple implementations of %s were found in the following providers: %s",
                commandClassSemantics.toString(), providersIdentification.toString());
    }

    public MultipleCommandImplementationsException(
            final CommandClassSemantics commandClassSemantics,
            final List<CommandInstantiationToken> commandInstantiationTokens) {
        super(createMessage(commandClassSemantics, commandInstantiationTokens));
    }
}
