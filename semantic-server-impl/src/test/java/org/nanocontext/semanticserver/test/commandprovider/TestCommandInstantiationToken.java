package org.nanocontext.semanticserver.test.commandprovider;

import org.nanocontext.semanticserverapi.core.commandprovider.CommandInstantiationToken;
import org.nanocontext.semanticserverapi.core.commandprovider.CommandProvider;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

/**
 * Created by cbeckey on 11/17/15.
 */
public class TestCommandInstantiationToken
        implements CommandInstantiationToken {

    private final CommandProvider commandProvider;
    private final Class<? extends Callable> commandClass;
    private final Constructor ctor;

    TestCommandInstantiationToken(
            final CommandProvider commandProvider,
            final Class<? extends Callable> commandClass,
            final Constructor ctor) {
        this.commandProvider = commandProvider;
        this.commandClass = commandClass;
        this.ctor = ctor;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return this.commandProvider;
    }

    public Class<? extends Callable> getCommandClass() {
        return commandClass;
    }

    public Constructor getCtor() {
        return ctor;
    }
}
