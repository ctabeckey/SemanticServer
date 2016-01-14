package com.paypal.credit.processors;

import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflow.subjects.AuthorizationId;

public abstract class AbstractProcessor<R extends RSProcessorContext>
        implements RSProcessor<R> {

    @Override
    public boolean process(final R rsProcessorContext) throws RSWorkflowException {
        AuthorizationId authId = (AuthorizationId)rsProcessorContext.get("authorizationId");

        System.out.println(
                String.format("[%s]Greetings from %s.process(%s)",
                        Thread.currentThread().getName(),
                        this.getClass().getSimpleName(),
                        authId == null ? "<null>" : authId.toString())
        );
        return true;
    }
}
