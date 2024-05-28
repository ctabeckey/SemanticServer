package com.paypal.credit.processors.test;

import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;

import java.io.PrintWriter;

public abstract class AbstractProcessor<R extends RSProcessorContext>
        implements RSProcessor<R> {

    private boolean verbose;
    private PrintWriter verboseDestination;

    public AbstractProcessor() {
        this(false, new PrintWriter(System.out));
    }

    public AbstractProcessor(final boolean verbose, final PrintWriter verboseDestination) {
        this.verbose = verbose;
        this.verboseDestination = verboseDestination;
    }

    @Override
    public boolean process(final R rsProcessorContext) throws RSWorkflowException {
        if (verbose && verboseDestination != null) {
            verboseDestination.println(
                    String.format("[%s]Greetings from %s.process",
                            Thread.currentThread().getName(),
                            this.getClass().getSimpleName())
            );
        }
        return true;
    }
}
