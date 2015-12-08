package com.paypal.credit.processors;

import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflowtest.model.AuthorizationId;

/**
 * Created by cbeckey on 11/16/15.
 */
public class ProcessorOne
        implements RSProcessor<RSProcessorContext> {

    @Override
    public boolean process(final RSProcessorContext rsProcessorContext) throws RSWorkflowException {
        AuthorizationId authId = (AuthorizationId)rsProcessorContext.get("authorizationId");

        System.out.println(
                String.format("Greetings from %s.process(%s)", this.getClass().getSimpleName(), authId == null ? "<null>" : authId.toString())
        );
        return true;
    }
}
