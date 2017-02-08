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
        T instance = createBeanInstance();
        return instance;
    }

}
