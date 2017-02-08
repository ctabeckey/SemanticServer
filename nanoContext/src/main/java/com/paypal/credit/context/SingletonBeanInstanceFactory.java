package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeanType;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cbeckey on 2/8/16.
 */
public class SingletonBeanInstanceFactory<T> extends AbstractBeanInstanceFactory<T> {
    /**  */
    private final ReentrantLock singletonInstantiationLock = new ReentrantLock();
    /** */
    private T singleton = null;

    public SingletonBeanInstanceFactory(
            final Context context,
            final String id,
            final String artifactIdentifier,
            final String clazzName,
            final String factoryId,
            final String factoryClassName,
            final String factoryMethodName,
            final boolean active,
            final String activateMethod,
            final List<AbstractProperty> ctorArgs)
            throws ContextInitializationException {
        super(context, id, artifactIdentifier, clazzName, factoryId, factoryClassName, factoryMethodName, active, activateMethod, ctorArgs);
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    @Override
    public T getValue() throws ContextInitializationException {
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
