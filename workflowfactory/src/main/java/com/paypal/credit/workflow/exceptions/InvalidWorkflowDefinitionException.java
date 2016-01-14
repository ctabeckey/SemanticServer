package com.paypal.credit.workflow.exceptions;

/**
 * Created by cbeckey on 11/13/15.
 */
public class InvalidWorkflowDefinitionException
        extends InvalidWorkflowException {
    private static String createMessage(Object processorNode) {
        return String.format("Loading workflow failed, unknown node type. %s", processorNode.toString());
    }

    public InvalidWorkflowDefinitionException(final Object processorNode) {
        super(createMessage(processorNode));
    }
}
