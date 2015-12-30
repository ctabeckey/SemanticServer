package com.paypal.credit.workflowcommand.workflow.exceptions;

/**
 * Created by cbeckey on 12/14/15.
 */
public abstract class WorkflowContextException extends Exception {

    protected WorkflowContextException() {

    }

    public WorkflowContextException(final String msg) {
        super(msg);
    }

    public WorkflowContextException(final Throwable t) {
        super(t);
    }
}
