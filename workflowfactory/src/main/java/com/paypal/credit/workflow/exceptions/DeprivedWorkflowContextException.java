package com.paypal.credit.workflow.exceptions;

/**
 * Created by cbeckey on 12/14/15.
 */
public class DeprivedWorkflowContextException extends WorkflowContextException {
    private static String createMessage(final Class<?> processorClass,
                                        final Class<?> requiredValidationClass) {
        return String.format("Processor %s requires %s, but it is not available at the processors location in this workflow.",
                processorClass == null ? "<unknown>" : processorClass.getName(),
                requiredValidationClass == null ? "<unknown>" : requiredValidationClass.getName());
    }

    public DeprivedWorkflowContextException(final Class<?> processorClass,
                                            final Class<?> requiredValidationClass) {
        super(createMessage(processorClass, requiredValidationClass));
    }
}
