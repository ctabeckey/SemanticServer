package com.paypal.credit.workflowcommand.workflow.exceptions;

/**
 * Created by cbeckey on 12/14/15.
 */
public class ProcessorProvidesAndRemovesException extends WorkflowContextException {
    private static String createMessage(Class<?> processorClass, Class<?> validationClass) {
        return String.format(
                "The processor %s is annotated as providing and removing %s. This confuses the code and is therefore not allowed.",
                processorClass == null ? "<unknown>" : processorClass.getName(),
                validationClass == null ? "<unknown>" : validationClass.getName()
        );
    }

    public ProcessorProvidesAndRemovesException(Class<?> processorClass, Class<?> validationClass) {
        super(createMessage(processorClass, validationClass));
    }
}
