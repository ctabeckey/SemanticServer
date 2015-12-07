package com.paypal.credit.test.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationTokenImpl;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.commandprovider.CommandLocationTokenRankedSet;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.test.commands.PostAuthorizationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
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

    /**
     * The publisher can be used to select a specific CommandProvider if
     * there are multiple Command implementations available.
     *
     * @return
     */
    @Override
    public String getPublisher() {
        return "test";
    }

    @Override
    public CommandInstantiationToken findCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {

        for (Class<? extends Command<?>> commandClass : availableCommands) {
            if (commandClassSemantics.describes(commandClass)) {
                try {
                    Constructor ctor = commandClass.getConstructor(parameters);
                    return new TestCommandInstantiationToken(this, commandClass, ctor);
                } catch (NoSuchMethodException e) {
                    // just ignore and go on ...
                }
            }
        }

        return null;
    }

    @Override
    public Command<?> createCommand(
            final CommandInstantiationToken commandInstantiationToken,
            final Object[] parameters) {
        if (commandInstantiationToken instanceof TestCommandInstantiationToken) {
            TestCommandInstantiationToken token = (TestCommandInstantiationToken)commandInstantiationToken;
            try {
                return (Command<?>) token.getCtor().newInstance(parameters);
            } catch (Exception x) {
                x.printStackTrace();
                LOGGER.error("Failed to create a Command instance from constructor, %s", x.getMessage());
            }
        } else {
            LOGGER.error("Invalid CommandInstantiationToken for this instance of CommandProvider");
            return null;
        }

        return null;
    }
}
