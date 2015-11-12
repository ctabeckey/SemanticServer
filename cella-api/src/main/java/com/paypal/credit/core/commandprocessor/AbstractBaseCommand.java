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

    /**
     * Make the routing token available so that the command
     * can direct the call.
     *
     * @param routingToken
     * @return
     */
    @Override
    public void setRoutingToken(final RoutingToken routingToken) {
        this.routingToken = routingToken;
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }

    public RoutingToken getRoutingToken() {
        return routingToken;
    }
}
