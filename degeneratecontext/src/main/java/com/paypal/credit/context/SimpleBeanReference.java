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
    final private ContextFactory contextFactory;

    /**
     * @param contextFactory
     * @param referenceType
     */
    protected SimpleBeanReference(final ContextFactory contextFactory, final ReferenceType referenceType) throws ContextInitializationException {
        this.contextFactory = contextFactory;
        this.referenceType = referenceType;
    }

    /**
     *
     * @return
     */
    public String getReferencedBeanIdentifier() {
        return this.referenceType.getBean();
    }

    /**
     * @return
     */
    T getBeanInstance() throws ContextInitializationException {
        AbstractBeanReference beanRef = contextFactory.findBeanReference(getReferencedBeanIdentifier());
        return (T) (beanRef == null ? null : beanRef.getBeanInstance());
    }

}
