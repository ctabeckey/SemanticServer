package com.paypal.credit.context;

/**
 * Created by cbeckey on 2/5/16.
 */
public class ExternalBeanDefinition<T> {
    private final String identifier;
    private final T beanInstance;

    public ExternalBeanDefinition(final String identifier, final T beanInstance) {
        this.identifier = identifier;
        this.beanInstance = beanInstance;
    }

    public String getIdentifier() {
        return identifier;
    }

    public T getBeanInstance() {
        return beanInstance;
    }
}
