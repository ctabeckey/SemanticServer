package com.paypal.credit.core.commandprocessor;

/**
 * Created by cbeckey on 11/11/15.
 */
public abstract class AbstractBaseCommand<R>
implements Command<R> {
    private CommandContext commandContext;
    private RoutingToken routingToken;

    /**
     * Provides environment access to the Command implementations.
     *
     * @param commandContext
     */
    @Override
    public void setCommandContext(final CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }

    public RoutingToken getRoutingToken() {
        return routingToken;
    }
}
