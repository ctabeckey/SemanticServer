package com.paypal.credit.workflow.exceptions;

import com.paypal.credit.utility.CompositeExceptionDelegate;

/**
 * Created by cbeckey on 12/14/15.
 */
public class CompositeWorkflowContextException extends WorkflowContextException {
    private CompositeExceptionDelegate<WorkflowContextException> compositeDelegate;

    public CompositeWorkflowContextException() {
        compositeDelegate = new CompositeExceptionDelegate<>();
    }

    public void add(WorkflowContextException workflowContextException) {
        compositeDelegate.addException(workflowContextException);
    }

    public boolean hasExceptions() {
        return this.compositeDelegate.hasExceptions();
    }

    @Override
    public String toString() {
        return this.compositeDelegate.toString();
    }

    @Override
    public String getMessage() {
        return this.compositeDelegate.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return this.compositeDelegate.getLocalizedMessage();
    }
}
