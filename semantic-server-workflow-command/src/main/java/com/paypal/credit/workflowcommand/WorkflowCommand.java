package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.AbstractApplicationAwareCommand;
import com.paypal.credit.core.commandprocessor.AsynchronouslyExecutableCommand;
import com.paypal.credit.core.commandprocessor.ApplicationAwareCommand;
import com.paypal.credit.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.Workflow;

import java.util.concurrent.Callable;

/**
 * The WorkflowCommand is just a container of the Workflow and the
 * parameters of the call.
 *
 * @param <C> the processor context type
 * @param <R> the result type
 *
 * Annotated with:
 * AsynchronouslyExecutable - allows client facades to specify asynchronous execution of this command
 * SingletonCommand - allows caching and re-use of instances of this class
 */
@AsynchronouslyExecutableCommand
public class WorkflowCommand<C extends RSProcessorContext, R>
    extends AbstractApplicationAwareCommand
    implements Callable<R>, ApplicationAwareCommand {

    private final Workflow<C, R> workflow;
    private final RSProcessorContext processorContext;

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
     * A synchronous execution of this command.
     * Asynchronous execution is managed by the CommandProcessor, this is the
     * (only) invocation of the command.
     *
     * @return
     */
    @Override
    public R call() throws Exception {
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
