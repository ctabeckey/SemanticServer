package com.paypal.credit.workflow.exceptions;

/**
 * Created by cbeckey on 11/13/15.
 */
public class UnknownWorkflowProcessorException extends InvalidWorkflowException {
    private static String createMessage(final String className) {
        return String.format("The processor implementation %s is not available.", className);
    }

    public UnknownWorkflowProcessorException(final String className) {
        super(createMessage(className));
    }
}
