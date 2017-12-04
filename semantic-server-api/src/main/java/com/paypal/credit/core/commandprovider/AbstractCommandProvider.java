package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.exceptions.CommandProviderException;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.utility.TypeAndInstanceUtility;

import java.util.concurrent.Callable;

/**
 * Created by cbeckey on 2/2/17.
 */
public abstract class AbstractCommandProvider
        implements CommandProvider {

    /**
     * This is a shorthand for a findCommand and createCommand in one
     * operation.
     */
    @Override
    public <R, C extends Callable<R>> C createCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Object[] parameters,
            final Class<R> resultType)
            throws CommandProviderException {
        final Class<?>[] parameterTypes = TypeAndInstanceUtility.getTypes(parameters);

        CommandInstantiationToken commandInstantiationToken =
                findCommand(routingToken, commandClassSemantics, parameterTypes, resultType);
        if (commandInstantiationToken != null) {
            return (C) createCommand(commandInstantiationToken, parameters);
        }

        return null;
    }
}
