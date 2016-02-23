package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.utility.references.Derivations;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * A degenerate IoC Context.
 */
public class Context {
    // ================================================================================
    // Instance Members
    // ================================================================================
    private Map<String, AbstractBeanReference> contextObjectsNameMap;

    Context(final Map<String, AbstractBeanReference> contextObjectsNameMap) {
        this.contextObjectsNameMap = contextObjectsNameMap;
    }

    /**
     *
     * @param identifier
     * @param bean
     */
    private synchronized void addBeanToContext(final String identifier, final AbstractBeanReference bean) {
        String id = identifier == null ?
                UUID.randomUUID().toString() :
                identifier;
        contextObjectsNameMap.put(id, bean);
    }

    /**
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final Class<T> beanClass) throws ContextInitializationException {
        // find the most specific bean in the context by type
        int minDistance = Integer.MAX_VALUE;
        AbstractBeanReference<T> selectedBeanReference = null;
        for (AbstractBeanReference<?> beanReference : contextObjectsNameMap.values()) {
            Object bean = null;
            int beanDistance;

            if (beanReference instanceof BeanFactoryReference) {
                BeanFactoryReference bfr = (BeanFactoryReference)beanReference;
                beanDistance = Derivations.distance(bfr.getClazz(), beanClass);
            } else {
                if (beanReference instanceof SimpleBeanReference){
                    bean = ((SimpleBeanReference) beanReference).getBeanInstance();

                } else if (beanReference instanceof ResolvedBeanReference){
                    bean = ((ResolvedBeanReference) beanReference).getBeanInstance();
                }

                beanDistance = Derivations.instanceDistance(bean, beanClass);
            }

            if (beanDistance < minDistance) {
                minDistance = beanDistance;
                selectedBeanReference = (AbstractBeanReference<T>) beanReference;
            }
        }

        return (T)selectedBeanReference.getBeanInstance();
    }

    /**
     *
     * @param name
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final String name, final Class<T> beanClass)
            throws ContextInitializationException {
        AbstractBeanReference<T> beanReference = contextObjectsNameMap.get(name);
        T bean = beanReference.getBeanInstance();
        if (beanClass == null || beanClass.isInstance(bean)) {
            return bean;
        }

        return null;
    }
}

