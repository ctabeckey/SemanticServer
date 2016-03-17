package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 2/4/16.
 */
public class FailedToInstantiateBeanException extends ContextInitializationException {
    private static String createMessage(String clazzName) {
        return String.format("Found but failed to instantiate referenced bean class %s", clazzName == null ? "<null>" : clazzName);
    }

    public FailedToInstantiateBeanException(String clazzName, Exception x) {
        super(createMessage(clazzName), x);
    }
}
