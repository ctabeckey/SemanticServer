package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.CommandContext;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.workflow.*;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;

/**
 * Created by cbeckey on 11/12/15.
 */
public class WorkflowCommand<T extends RSProcessorContext, R>
implements Command<R> {
    // ===========================================================================
    // Instance Members
    // ===========================================================================

    private final RSSerialController<T> startController;
    private final T context;

    private CommandContext commandContext;
    private RoutingToken routingToken;

    WorkflowCommand(final RSSerialController<T> startController, final T context) {
        this.context = context;
        this.startController = startController;
    }

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

    /**
     * A synchronous execution of this command.
     * Asynchronous execution is managed by the CommandProcessor, this is the
     * (only) invocation of the command.
     *
     * @return
     */
    @Override
    public R invoke() throws RSWorkflowException {
        this.startController.process(this.context);
        return null;
    }
}
