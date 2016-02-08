package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.ReferenceType;

/**
 * A placeholder that is stuffed into the context when a ref element is encountered.
 * This instance will be replaced on the first use of the bean with a reference to
 * the actual instance.
 */
final class SimpleBeanReference<T> extends AbstractBeanReference<T> {
    /** */
    final private ReferenceType referenceType;

    /**
     * @param referenceType
     */
    protected SimpleBeanReference(final ReferenceType referenceType) throws ContextInitializationException {
        this.referenceType = referenceType;
    }

    /**
     * @param ctx
     * @param clazz
     * @return
     */
    @Override
    T getBeanInstance(final Context ctx, final Class<T> clazz) throws ContextInitializationException {
        return ctx.getBean(this.referenceType.getBean(), clazz);
    }
}
