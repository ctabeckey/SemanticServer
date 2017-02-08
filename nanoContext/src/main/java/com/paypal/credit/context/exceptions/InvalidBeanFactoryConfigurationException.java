package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 1/23/17.
 */
public class InvalidBeanFactoryConfigurationException extends ContextInitializationException {
    private static String createMessage(final String beanIdentifier){
        return String.format("For bean %s, factory class or bean was specified without factory method name", beanIdentifier);
    }

    public InvalidBeanFactoryConfigurationException(final String beanIdentifier) {
        super(createMessage(beanIdentifier));
    }

    public InvalidBeanFactoryConfigurationException(final String beanIdentifier, final Throwable t) {
        super(createMessage(beanIdentifier), t);
    }
}
