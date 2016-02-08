package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeanType;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cbeckey on 2/8/16.
 */
public class SingletonBeanFactoryReference<T> extends BeanFactoryReference<T> {
    /**  */
    private final ReentrantLock singletonInstantiationLock = new ReentrantLock();
    /** */
    private T singleton = null;

    /**
     *
     * @param beanReferenceFactory
     * @param beanType
     * @throws ContextInitializationException
     */
    protected SingletonBeanFactoryReference(final BeanReferenceFactory beanReferenceFactory, final BeanType beanType) throws ContextInitializationException {
        super(beanReferenceFactory, beanType);
    }

    @Override
    T getBeanInstance(final Context ctx, final Class clazz) throws ContextInitializationException {
        singletonInstantiationLock.lock();
        try {
            if (singleton == null) {
                singleton = createBeanInstance();
            }
        } finally {
            singletonInstantiationLock.unlock();
        }
        return singleton;
    }
}
