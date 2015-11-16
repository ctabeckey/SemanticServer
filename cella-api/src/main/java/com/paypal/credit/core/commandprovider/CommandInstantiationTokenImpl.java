package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;

import java.lang.reflect.Constructor;

/**
 * A default implementation of the CommandInstantiationToken interface.
 *
 * @see com.paypal.credit.core.commandprovider.CommandInstantiationToken
 */
public class CommandInstantiationTokenImpl
        implements CommandInstantiationToken {
    private final CommandProvider commandProvider;
    private final Class<? extends Command> command;
    private final Constructor<?> ctor;      // one of ctor or factory must be non-null
    private final CommandFactory factory;   // if both are non-null then ctor is ignored

    public CommandInstantiationTokenImpl(
            final CommandProvider commandProvider,
            final Class<? extends Command> command,
            final Constructor<?> ctor) {
        this.commandProvider = commandProvider;
        this.command = command;
        this.ctor = ctor;
        this.factory = null;
    }

    public CommandInstantiationTokenImpl(
            final CommandProvider commandProvider,
            final CommandFactory factory) {
        this.commandProvider = commandProvider;
        this.command = null;
        this.ctor = null;
        this.factory = factory;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return this.commandProvider;
    }

    public Class<? extends Command> getCommand() {
        return command;
    }

    public Constructor<?> getCtor() {
        return ctor;
    }

    public CommandFactory getFactory() {
        return factory;
    }
}
