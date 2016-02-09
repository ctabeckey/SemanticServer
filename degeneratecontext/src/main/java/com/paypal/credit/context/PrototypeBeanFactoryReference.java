package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeanType;

/**
 * Created by cbeckey on 2/8/16.
 */
public class PrototypeBeanFactoryReference<T> extends BeanFactoryReference<T> {

    /**
     *
     * @param contextFactory
     * @param beanType
     * @throws ContextInitializationException
     */
    protected PrototypeBeanFactoryReference(final ContextFactory contextFactory, final BeanType beanType) throws ContextInitializationException {
        super(contextFactory, beanType);
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    @Override
    T getBeanInstance() throws ContextInitializationException {
        T instance = createBeanInstance();
        return instance;
    }
}
