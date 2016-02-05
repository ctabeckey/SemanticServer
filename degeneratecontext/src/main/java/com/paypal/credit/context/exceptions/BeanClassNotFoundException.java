package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/4/16.
 */
public class BeanClassNotFoundException extends ContextInitializationException {
    private static String createMessage(String clazzName) {
        return String.format("Unable to find referenced bean class %s", clazzName == null ? "<null>" : clazzName);
    }

    public BeanClassNotFoundException(String clazzName) {
        super(createMessage(clazzName));
    }
}
