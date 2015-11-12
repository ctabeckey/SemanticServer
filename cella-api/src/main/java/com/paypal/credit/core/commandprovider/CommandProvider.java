package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;

import java.util.Set;

/**
 * Created by cbeckey on 11/11/15.
 */
public interface CommandProvider {

    ConstructorRankingList findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType);

    }
