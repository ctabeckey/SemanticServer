package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;

/**
 * When a CommandProvider provides a factory for a Command rather than a constructor
 * in a CommandLocationToken, then the provided Factory MUST implement this interface
 */
public interface CommandFactory {
    /**
     * Create a Command
     * @param routingToken
     * @param parameters
     * @return
     * @throws Exception
     */
    Command<?> create(
            final RoutingToken routingToken,
            final Object[] parameters)
    throws Exception;

}
