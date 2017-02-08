package com.paypal.credit.context;

/**
 * Created by cbeckey on 1/23/17.
 */
public class GenericBeanFactory {
    public static GenericBeanInstance createBean() {
        return new GenericBeanInstance();
    }

    public GenericBeanInstance createBeanInstance() {
        return new GenericBeanInstance();
    }

}
