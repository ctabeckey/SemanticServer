package com.paypal.credit.workflow.exceptions;

/**
 * Root exception for all exceptions thrown when a workflow could not be instantiated.
 */
public abstract class InvalidWorkflowException
extends Exception {

    public InvalidWorkflowException(final String message) {
        super(message);
    }

    public InvalidWorkflowException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
