package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.AsynchronouslyExecutable;
import com.paypal.credit.core.commandprocessor.CellaAwareCommand;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.RSProcessorContext;

import java.util.concurrent.Callable;

/**
 * The WorkflowCommand is just a container of the Workflow and the
 * parameters of the call.
 *
 * @param <C>
 * @param <R>
 */
@AsynchronouslyExecutable
public class WorkflowCommand<C extends RSProcessorContext, R>
implements Callable<R>, CellaAwareCommand {
    private final Workflow<C, R> workflow;
    private final RSProcessorContext processorContext;
    private Application application;

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
     * @param application
     */
    @Override
    public void setApplicationContext(final Application application) {
        this.application = application;
    }

    public Application getApplicationContext() {
        return this.application;
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
