package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 1/20/17.
 */
public class InvalidFactoryIdentifierException extends ContextInitializationException {
    private static String createMessage(final String beanClassName, String factoryId) {
        return String.format("Unable to create instance of %s, factory ID %s could not be found", beanClassName, factoryId);
    }

    private static String createMessage(final String beanClassName, String factoryId, String factoryMethodName) {
        return String.format("Unable to create instance of %s, factory %s did not have method %s", beanClassName, factoryId, factoryMethodName);
    }

    public <T> InvalidFactoryIdentifierException(final String beanClassName, String factoryId) {
        super(createMessage(beanClassName, factoryId));
    }

    public <T> InvalidFactoryIdentifierException(final String beanClassName, String factoryId, Throwable t) {
        super(createMessage(beanClassName, factoryId), t);
    }

    public <T> InvalidFactoryIdentifierException(final String beanClassName, String factoryId, String factoryMethodName) {
        super(createMessage(beanClassName, factoryId, factoryMethodName));
    }

    public <T> InvalidFactoryIdentifierException(final String beanClassName, String factoryId, String factoryMethodName, Throwable t) {
        super(createMessage(beanClassName, factoryId, factoryMethodName), t);
    }

}
