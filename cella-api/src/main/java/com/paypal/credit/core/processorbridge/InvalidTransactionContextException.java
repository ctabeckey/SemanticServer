package com.paypal.credit.core.processorbridge;

/**
 * Created by cbeckey on 11/12/15.
 */
public class InvalidTransactionContextException extends Exception {
    private static String createMessage(final String missingField) {
        return String.format("TransactionContext is missing the required value '%s'", missingField);
    }

    public InvalidTransactionContextException(final String missingField) {
        super(createMessage(missingField));
    }
}
