package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.GenericContextInitializationException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ReferenceType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
class BeanReferenceFactory {
    /** */
    private final Map<String, AbstractBeanReference> beanReferences = new HashMap<>();

    /**
     *
     * @param beans
     * @return
     * @throws ContextInitializationException
     */
    Map<String, AbstractBeanReference> create(final BeansType beans) throws ContextInitializationException {
        // create the top-level beans (those directly under the 'beans' element)
        // AbstractBeanReference will create the dependencies under each top level
        // bean
        for (BeanType beanType : beans.getBean()) {
            createBeanReference(beanType);
        }

        return beanReferences;
    }

    /**
     *
     * @param beanType
     * @return
     * @throws ContextInitializationException
     */
    AbstractBeanReference createBeanReference(final BeanType beanType)
            throws ContextInitializationException {
        AbstractBeanReference beanReference = null;

        switch(beanType.getScope()) {
            case PROTOTYPE: {
                beanReference = new PrototypeBeanFactoryReference(this, beanType);
                break;
            }
            case SINGLETON: {
                beanReference = new SingletonBeanFactoryReference(this, beanType);
                break;
            }
        }

        if (beanReference == null) {
            throw new GenericContextInitializationException(
                    String.format("Unrecognized scope specifier (%s) in context definition for bean %s", beanType.getScope(), beanType.getId())
            );
        }

        // add the bean and an ID (perhaps synthetic) to the context Set
        String id = beanType.getId() == null ? UUID.randomUUID().toString() : beanType.getId();
        beanReferences.put(id, beanReference);

        return beanReference;
    }

    /**
     *
     * @param reference
     * @return
     * @throws ContextInitializationException
     */
    AbstractBeanReference createBeanReference(final ReferenceType reference)
            throws ContextInitializationException {
        SimpleBeanReference beanReference = new SimpleBeanReference(reference);
        return beanReference;
    }
}
