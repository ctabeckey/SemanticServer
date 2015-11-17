package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.commandprovider.exceptions.InvalidTokenException;
import com.paypal.credit.core.semantics.CommandClassSemantics;

/**
 * Created by cbeckey on 11/11/15.
 */
public interface CommandProvider {

    CommandLocationTokenRankedSet findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType);

    Command<?> createCommand(
            final RoutingToken routingToken,
            final CommandInstantiationToken commandInstantiationToken,
            final Object[] parameters)
            throws CommandInstantiationException, InvalidTokenException;

}
