package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.CommandContext;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.RSProcessorContext;

/**
 * The WorkflowCommand is just a container of the Workflow and the
 * parameters of the call.
 *
 * @param <C>
 * @param <R>
 */
public class WorkflowCommand<C extends RSProcessorContext, R>
implements Command<R> {
    private final Workflow<C, R> workflow;
    private final RSProcessorContext processorContext;
    private CommandContext commandContext;

    /**
     *
     * @param workflow
     * @param processorContext
     */
    WorkflowCommand(final Workflow<C, R> workflow, final RSProcessorContext processorContext) {
        ParameterCheckUtility.checkParameterNotNull(workflow, "workflow");
        ParameterCheckUtility.checkParameterNotNull(processorContext, "processorContext");

        this.workflow = workflow;
        this.processorContext = processorContext;
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

    public CommandContext getCommandContext() {
        return commandContext;
    }

    /**
     * A synchronous execution of this command.
     * Asynchronous execution is managed by the CommandProcessor, this is the
     * (only) invocation of the command.
     *
     * @return
     */
    @Override
    public R invoke() throws Throwable {

        if (this.workflow.execute((C)this.processorContext)) {
            return extractResultFromContext(processorContext);
        }

        return null;
    }

    /**
     *
     * @param processorContext
     * @return
     */
    private R extractResultFromContext(final RSProcessorContext processorContext) {

        return null;
    }
}
