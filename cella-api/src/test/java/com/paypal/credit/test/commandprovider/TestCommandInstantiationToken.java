package com.paypal.credit.test.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandProvider;

import java.lang.reflect.Constructor;

/**
 * Created by cbeckey on 11/17/15.
 */
public class TestCommandInstantiationToken
        implements CommandInstantiationToken {

    private final CommandProvider commandProvider;
    private final Class<? extends Command> commandClass;
    private final Constructor ctor;

    TestCommandInstantiationToken(
            final CommandProvider commandProvider,
            final Class<? extends Command> commandClass,
            final Constructor ctor) {
        this.commandProvider = commandProvider;
        this.commandClass = commandClass;
        this.ctor = ctor;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return this.commandProvider;
    }

    public Class<? extends Command> getCommandClass() {
        return commandClass;
    }

    public Constructor getCtor() {
        return ctor;
    }
}
