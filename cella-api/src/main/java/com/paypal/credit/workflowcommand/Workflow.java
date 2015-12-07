package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.CommandContext;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.workflow.*;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;

/**
 * Created by cbeckey on 11/12/15.
 */
public class Workflow<C extends RSProcessorContext, R> {
    // ===========================================================================
    // Instance Members
    // ===========================================================================

    private final RSSerialController<C> startController;
    private final Class<R> resultType;

    private CommandContext commandContext;
    private RoutingToken routingToken;

    Workflow(final RSSerialController<C> startController, final Class<R> resultType) {
        this.startController = startController;
        this.resultType = resultType;
    }

    /**
     * Execute the workflow with the given parameters
     *
     * @return
     * @throws RSWorkflowException
     */
    public boolean execute(C context) throws RSWorkflowException {
        return this.startController.process(context);
    }

}
