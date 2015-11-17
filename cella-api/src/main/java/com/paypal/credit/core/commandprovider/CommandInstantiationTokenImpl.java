package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;

import java.lang.reflect.Constructor;
import java.util.Objects;

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

    //==========================================================================================
    // .equals() and .hashCode() must be correctly implemented, this class may be used
    // as a Set element or a Map key
    //==========================================================================================

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandInstantiationTokenImpl that = (CommandInstantiationTokenImpl) o;
        return Objects.equals(getCommandProvider(), that.getCommandProvider()) &&
                Objects.equals(getCommand(), that.getCommand()) &&
                Objects.equals(getCtor(), that.getCtor()) &&
                Objects.equals(getFactory(), that.getFactory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandProvider(), getCommand(), getCtor(), getFactory());
    }
}
