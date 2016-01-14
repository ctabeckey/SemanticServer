package com.paypal.credit.workflow.exceptions;

import com.paypal.credit.workflow.RSProcessor;

/**
 * Created by cbeckey on 11/13/15.
 */
public class InvalidWorkflowProcessorException extends InvalidWorkflowException {
    private static String createMessage(final String className) {
        return String.format(
                "The processor implementation %s is not a valid processor, it does not implement %s.",
                className,
                RSProcessor.class.getName());
    }

    public InvalidWorkflowProcessorException(final String className) {
        super(createMessage(className));
    }
}
