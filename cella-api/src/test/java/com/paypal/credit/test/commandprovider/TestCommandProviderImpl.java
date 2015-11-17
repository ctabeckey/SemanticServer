package com.paypal.credit.test.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandFactory;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationTokenImpl;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.CommandLocationTokenRankedSet;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.test.commands.PostAuthorizationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbeckey on 11/11/15.
 */
public class TestCommandProviderImpl
implements CommandProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestCommandProviderImpl.class);
    private static final Set<Class<? extends Command<?>>> availableCommands;

    static {
        availableCommands = new HashSet<>();
        availableCommands.add(PostAuthorizationCommand.class);
    }

    public TestCommandProviderImpl() {
    }

    @Override
    public CommandLocationTokenRankedSet findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {
        CommandLocationTokenRankedSet commandList =
                new CommandLocationTokenRankedSet(routingToken, commandClassSemantics, parameters, resultType);

        for (Class<? extends Command<?>> commandClass : availableCommands) {
            try {
                commandList.add(this, routingToken, commandClass, commandClass.getConstructor(parameters));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return commandList;
    }

    @Override
    public Command<?> createCommand(
            final RoutingToken routingToken,
            final CommandInstantiationToken commandInstantiationToken,
            final Object[] parameters) {
        if (commandInstantiationToken instanceof CommandInstantiationTokenImpl) {
            CommandInstantiationTokenImpl token = (CommandInstantiationTokenImpl)commandInstantiationToken;
            if (token.getFactory() != null) {
                try {
                    return token.getFactory().create(routingToken, parameters);
                } catch (Exception x) {
                    x.printStackTrace();
                    LOGGER.error("Failed to create a Command instance from CommandFactory, %s", x.getMessage());
                }
            } else if (token.getCtor() != null) {
                try {
                    return (Command<?>) token.getCtor().newInstance(parameters);
                } catch (Exception x) {
                    x.printStackTrace();
                    LOGGER.error("Failed to create a Command instance from constructor, %s", x.getMessage());
                }
            }
        } else {
            LOGGER.error("Invalid CommandInstantiationToken for this instance of CommandProvider");
            return null;
        }

        return null;
    }
}
