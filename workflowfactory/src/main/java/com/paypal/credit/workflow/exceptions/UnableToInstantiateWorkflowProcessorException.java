package com.paypal.credit.workflow.exceptions;

/**
 * Created by cbeckey on 11/13/15.
 */
public class UnableToInstantiateWorkflowProcessorException
        extends InvalidWorkflowException {
    private static String createMessage(final String className) {
        return String.format("Unable to instantiate the processor implementation %s.", className);
    }

    public UnableToInstantiateWorkflowProcessorException(final String className) {
        super(createMessage(className));
    }

    public UnableToInstantiateWorkflowProcessorException(final String className, Throwable t) {
        super(createMessage(className), t);
    }
}
