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
            final List<AbstractProperty> ctorArgs)
            throws ContextInitializationException {
        super(context, id, artifactIdentifier, clazzName, ctorArgs);
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

    /**
     * Return an instance of the same class where the target resolution type is the
     * given type.
     * Derivations of this method MUST return <code>this</code> if the targetValueType is
     * exactly the same as the result of getValueType().
     *
     * @param targetValueType
     * @return
     * @see #isResolvableAs(Class) may be called to determine whether the morph will be
     * successful before calling this method.
     */
    @Override
    public <S> AbstractProperty<S> morph(Class<S> targetValueType) throws ContextInitializationException {
        return (AbstractProperty<S>) this;
    }
}
