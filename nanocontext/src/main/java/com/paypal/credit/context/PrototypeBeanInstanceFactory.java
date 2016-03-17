package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;

import java.util.List;

/**
 * Created by cbeckey on 2/8/16.
 */
public class PrototypeBeanInstanceFactory<T>
        extends AbstractBeanInstanceFactory<T> {

    /**
     *
     * @param context
     * @param id
     * @param artifactIdentifier
     * @param clazzName
     * @param ctorArgs
     * @throws ContextInitializationException
     */
    public PrototypeBeanInstanceFactory(
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
        T instance = createBeanInstance();
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> AbstractProperty<S> morph(Class<S> targetValueType) throws ContextInitializationException {
        return (AbstractProperty<S>) this;
    }
}
