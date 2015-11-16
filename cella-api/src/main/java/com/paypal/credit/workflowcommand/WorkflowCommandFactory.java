package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandFactory;
import com.paypal.credit.workflowcommand.exceptions.InvalidWorkflowException;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.RSSerialController;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cbeckey on 11/13/15.
 */
public class WorkflowCommandFactory
implements CommandFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(WorkflowCommandFactory.class);
    private final WorkflowType workflow;

    /**
     *
     * @param workflow
     */
    WorkflowCommandFactory(final WorkflowType workflow) {
        this.workflow = workflow;
    }

    /**
     *
     * @param routingToken
     * @param parameters
     * @return
     * @throws Exception
     */
    public Command<?> create(
            final RoutingToken routingToken,
            final Object[] parameters) throws Exception {
        return create(this.workflow, createProcessorContext(routingToken, parameters));
    }

    private <R, T extends RSProcessorContext> WorkflowCommand<T, R> create(
            final WorkflowType workflow,
            final T processorContext) throws InvalidWorkflowException {
        // create the workflow command
        RSSerialController<T> controller = WorkflowFactory.create(
                (Class<T>) processorContext.getClass(),
                workflow);
        return new WorkflowCommand(controller, processorContext);
    }

    // TODO: code this
    private RSProcessorContext createProcessorContext(
            final RoutingToken routingToken,
            final Object[] parameters) throws InvalidWorkflowException{
        RSProcessorContext processorContext = new RSProcessorContext() {

        };

        return processorContext;
    }
}
