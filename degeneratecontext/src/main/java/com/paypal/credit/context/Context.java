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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A degenerate IoC Context.
 */
public class Context {
    // ================================================================================
    // Instance Members
    // ================================================================================
    private enum STATE{NEW, INITIALIZING, INITIALIZED, ERROR}
    private final BeansType beans;
    private Map<String, AbstractBeanReference> contextObjectsNameMap;

    private STATE state = STATE.NEW;

    private Context(final BeansType beans) {
        this.beans = beans;
    }

    /**
     *
     */
    private synchronized void initialize() throws ContextInitializationException {
        this.state = STATE.INITIALIZING;

        BeanReferenceFactory beanFactory = new BeanReferenceFactory();
        contextObjectsNameMap = beanFactory.create(this.beans);

        this.state = STATE.INITIALIZED;
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
     * @param externalBeanDefinitions
     */
    private void addExternalBeanDefinitions(final ExternalBeanDefinition<?>[] externalBeanDefinitions)
            throws ContextInitializationException {
        if (externalBeanDefinitions == null) {
            return;
        }
        for (ExternalBeanDefinition<?> externalBeanDefinition : externalBeanDefinitions) {
            addBeanToContext(externalBeanDefinition.getIdentifier(), new ResolvedBeanReference(externalBeanDefinition.getBeanInstance()));
        }
    }

    /**
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final Class<T> beanClass) throws ContextInitializationException {
        if (state != STATE.INITIALIZED) {
            throw new IllegalStateException(
                    String.format("Context is not initialized, current state is %s", state)
            );
        }

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
        if (state != STATE.INITIALIZED) {
            throw new IllegalStateException(
                    String.format("Context is not initialized, current state is %s", state)
            );
        }

        AbstractBeanReference<T> beanReference = contextObjectsNameMap.get(name);
        T bean = beanReference.getBeanInstance();
        if (beanClass != null && beanClass.isInstance(bean)) {
            return bean;
        }

        return null;
    }

    // ================================================================================
    // Class (Static) Members
    // ================================================================================
    private static JAXBContext jaxbContext = null;
    private static final synchronized JAXBContext getJaxbContext()
            throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance("com.paypal.credit.context.xml");
        }
        return jaxbContext;
    }

    // ================================================================================
    // Creational Static Members
    // ================================================================================

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws FileNotFoundException
     */
    public final static Context create(final File contextDefinition, ExternalBeanDefinition<?>... externalBeanDefinitions)
            throws JAXBException, ContextInitializationException, FileNotFoundException {
        FileInputStream fiS = new FileInputStream(contextDefinition);
        try {
            return create(fiS, externalBeanDefinitions);
        } finally {
            try {fiS.close();} catch(Exception x){}     // eat the 'secondary' exception
        }
    }

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws IOException
     */
    public final static Context create(final URL contextDefinition, ExternalBeanDefinition<?>... externalBeanDefinitions)
            throws JAXBException, ContextInitializationException, IOException {
        InputStream urlIS = contextDefinition.openStream();
        try {
            return create(urlIS, externalBeanDefinitions);
        } finally {
            try {urlIS.close();} catch(Exception x){}     // eat the 'secondary' exception
        }
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     */
    public final static Context create(final InputStream inputStream, ExternalBeanDefinition<?>... externalBeanDefinitions)
            throws JAXBException, ContextInitializationException {
        JAXBContext jaxbContext = getJaxbContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<BeansType> ctx = (JAXBElement<BeansType>) unmarshaller.unmarshal(inputStream);
        return create(ctx.getValue(), externalBeanDefinitions);
    }

    /**
     *
     * @param beansType
     * @return
     * @throws ContextInitializationException
     */
    public final static Context create(final BeansType beansType, ExternalBeanDefinition<?>... externalBeanDefinitions)
            throws ContextInitializationException {
        if (beansType == null) {
            throw new IllegalArgumentException("BeansType is null and must not be.");
        }

        Context ctx = new Context(beansType);

        ctx.addExternalBeanDefinitions(externalBeanDefinitions);

        ctx.initialize();

        return ctx;
    }

}

