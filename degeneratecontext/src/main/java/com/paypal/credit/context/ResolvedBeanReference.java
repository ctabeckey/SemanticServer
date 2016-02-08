package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;

/**
 * The degenerate case of an AbstractBeanReference, where the bean is extant and its
 * reference is known.
 */
public class ResolvedBeanReference<T> extends AbstractBeanReference<T> {
    private final T bean;
    /**
     */
    protected ResolvedBeanReference(T bean) throws ContextInitializationException {
        this.bean = bean;
    }

    /**
     * @param ctx
     * @param clazz
     * @return
     */
    @Override
    T getBeanInstance(final Context ctx, final Class<T> clazz) throws ContextInitializationException {
        return bean;
    }
}
