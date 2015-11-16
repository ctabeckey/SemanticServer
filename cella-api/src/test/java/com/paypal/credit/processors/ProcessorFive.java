package com.paypal.credit.processors;

import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;

/**
 * Created by cbeckey on 11/16/15.
 */
public class ProcessorFive
        implements RSProcessor<RSProcessorContext> {

    @Override
    public boolean process(final RSProcessorContext rsProcessorContext) throws RSWorkflowException {
        System.out.println("Greetings from " + this.getClass().getSimpleName() + ".process()");
        return true;
    }
}
