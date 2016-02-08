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
     * @param ctx
     * @param clazz
     * @return
     */
    abstract T getBeanInstance(Context ctx, Class<T> clazz) throws ContextInitializationException;

}
