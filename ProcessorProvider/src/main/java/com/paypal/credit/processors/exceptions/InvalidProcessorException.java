package com.paypal.credit.processors.exceptions;

/**
 * Created by cbeckey on 1/8/16.
 */
public class InvalidProcessorException
extends ProcessorProviderException {
    private static String createMessage(Class<?> clazz) {
        return String.format("%s is not a valid Processor class", clazz.getName());
    }

    public InvalidProcessorException(Class<?> clazz) {
        super(createMessage(clazz));
    }
}
