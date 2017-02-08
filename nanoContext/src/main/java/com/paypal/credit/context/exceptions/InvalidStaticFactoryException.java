package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 1/20/17.
 */
public class InvalidStaticFactoryException extends ContextInitializationException {
    private static String createMessage(Class<?> beanClass, String factoryClassName) {
        return String.format("Unable to create instance of %s, factory %s could not be found", beanClass.getName(), factoryClassName);
    }

    private static String createMessage(Class<?> beanClass, String factoryClassName, String factoryMethodName) {
        return String.format("Unable to create instance of %s, factory class %s did not have static method %s", beanClass.getName(), factoryClassName, factoryMethodName);
    }

    public <T> InvalidStaticFactoryException(Class<T> beanClass, String factoryClassName) {
        super(createMessage(beanClass, factoryClassName));
    }

    public <T> InvalidStaticFactoryException(Class<T> beanClass, String factoryClassName, Throwable t) {
        super(createMessage(beanClass, factoryClassName), t);
    }

    public <T> InvalidStaticFactoryException(Class<T> beanClass, String factoryClassName, String factoryMethodName) {
        super(createMessage(beanClass, factoryClassName, factoryMethodName));
    }

    public <T> InvalidStaticFactoryException(Class<T> beanClass, String factoryClassName, String factoryMethodName, Throwable t) {
        super(createMessage(beanClass, factoryClassName, factoryMethodName), t);
    }

}
