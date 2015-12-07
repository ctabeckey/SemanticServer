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
public class WorkflowCommandFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(WorkflowCommandFactory.class);

    /**
     * @param workflowType
     * @param routingToken
     * @param parameters
     * @return
     * @throws Exception
     */
    static Command<?> create(
            final WorkflowType workflowType,
            final RoutingToken routingToken,
            final Object[] parameters)
            throws InvalidWorkflowException {
        return create(workflowType, createProcessorContext(routingToken, parameters));
    }

    private static <R, T extends RSProcessorContext> WorkflowCommand<T, R> create(
            final WorkflowType workflow,
            final T processorContext)
            throws InvalidWorkflowException {
        // create the workflow command
        RSSerialController<T> controller = WorkflowFactory.create(
                (Class<T>) processorContext.getClass(),
                workflow);
        return new WorkflowCommand(controller, processorContext);
    }

    // TODO: code this
    private static RSProcessorContext createProcessorContext(
            final RoutingToken routingToken,
            final Object[] parameters) {
        RSProcessorContext processorContext = new RSProcessorContext() {

        };

        return processorContext;
    }
}
