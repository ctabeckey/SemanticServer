package com.paypal.credit.test.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.ConstructorRankingList;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.test.commands.PostAuthorizationCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbeckey on 11/11/15.
 */
public class TestCommandProviderImpl
implements CommandProvider {
    private static final Set<Class<? extends Command<?>>> availableCommands;

    static {
        availableCommands = new HashSet<>();
        availableCommands.add(PostAuthorizationCommand.class);
    }

    public TestCommandProviderImpl() {
    }

    @Override
    public ConstructorRankingList findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {
        ConstructorRankingList commandList =
                new ConstructorRankingList(routingToken, commandClassSemantics.toString(), parameters, resultType);

        for (Class<? extends Command<?>> commandClass : availableCommands) {
            try {
                commandList.add(routingToken, commandClass, commandClass.getConstructor(parameters));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return commandList;
    }
}
