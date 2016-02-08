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
    final private BeanReferenceFactory beanReferenceFactory;

    /**
     * @param beanReferenceFactory
     * @param referenceType
     */
    protected SimpleBeanReference(final BeanReferenceFactory beanReferenceFactory, final ReferenceType referenceType) throws ContextInitializationException {
        this.beanReferenceFactory = beanReferenceFactory;
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
        AbstractBeanReference beanRef = beanReferenceFactory.findBeanReference(getReferencedBeanIdentifier());
        return (T) beanRef.getBeanInstance();
    }

}
