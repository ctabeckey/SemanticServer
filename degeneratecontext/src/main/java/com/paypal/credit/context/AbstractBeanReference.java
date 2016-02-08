package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.CircularReferenceException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeanType;

import java.util.HashSet;
import java.util.Set;

/**
 * The abstract placeholder class
 */
abstract class AbstractBeanReference<T> {
    /**
     */
    protected AbstractBeanReference() throws ContextInitializationException {
    }

    /**
     *
     * @return
     */
    abstract T getBeanInstance() throws ContextInitializationException;

}
