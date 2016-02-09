package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.GenericContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidElementTypeException;
import com.paypal.credit.context.exceptions.UnknownCollectionTypeException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ReferenceType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class ContextFactory {
    /** */
    private Map<String, AbstractBeanReference> contextObjectsNameMap = new HashMap<>();

    /** JAXB Context is created on demand */
    private JAXBContext jaxbContext = null;

    /** */
    private BeansType beansType;

    public ContextFactory() {
    }

    /**
     *
     * @param beansType
     * @return
     */
    public ContextFactory with(BeansType beansType) {
        this.beansType = beansType;

        return this;
    }

    /**
     *
     * @param identifier
     * @param bean
     */
    public synchronized ContextFactory withBean(final String identifier, final AbstractBeanReference bean) {
        String id = identifier == null ?
                UUID.randomUUID().toString() :
                identifier;
        contextObjectsNameMap.put(id, bean);

        return this;
    }

    /**
     *
     * @param externalBeanDefinition
     * @return
     * @throws ContextInitializationException
     */
    public ContextFactory withExternalBeanDefinition(final ExternalBeanDefinition<?> externalBeanDefinition)
            throws ContextInitializationException {
        if (externalBeanDefinition != null) {
            withBean(externalBeanDefinition.getIdentifier(), new ResolvedBeanReference(externalBeanDefinition.getBeanInstance()));
        }
        return this;
    }

    /**
     *
     * @param externalBeanDefinitions
     * @return
     * @throws ContextInitializationException
     */
    public ContextFactory withExternalBeanDefinitions(final ExternalBeanDefinition<?>[] externalBeanDefinitions)
            throws ContextInitializationException {
        if (externalBeanDefinitions != null) {
            for (ExternalBeanDefinition<?> externalBeanDefinition : externalBeanDefinitions) {
                withExternalBeanDefinition(externalBeanDefinition);
            }
        }
        return this;
    }

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws FileNotFoundException
     */
    public ContextFactory withContextDefinition(final File contextDefinition)
            throws JAXBException, ContextInitializationException, FileNotFoundException {
        FileInputStream fiS = new FileInputStream(contextDefinition);
        return withContextDefinition(fiS);
    }

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws IOException
     */
    public ContextFactory withContextDefinition(final URL contextDefinition)
            throws JAXBException, ContextInitializationException, IOException {
        InputStream urlIS = contextDefinition.openStream();
        return withContextDefinition(urlIS);
    }

    /**
     *
     * @param inputStream
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     */
    public ContextFactory withContextDefinition(final InputStream inputStream)
            throws JAXBException, ContextInitializationException {
        JAXBContext jaxbContext = getJaxbContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<BeansType> ctx = (JAXBElement<BeansType>) unmarshaller.unmarshal(inputStream);
        return with(ctx.getValue());
    }

    /**
     *
     * @return
     * @throws JAXBException
     */
    private final synchronized JAXBContext getJaxbContext()
            throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance("com.paypal.credit.context.xml");
        }
        return jaxbContext;
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    public Context build()
            throws ContextInitializationException {
        // create the top-level beansType (those directly under the 'beansType' element)
        // AbstractBeanReference will create the dependencies under each top level
        // bean
        for (BeanType beanType : beansType.getBean()) {
            createBeanReference(beanType);
        }

        return new Context(this.contextObjectsNameMap);
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
        contextObjectsNameMap.put(id, beanReference);

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
        SimpleBeanReference beanReference = new SimpleBeanReference(this, reference);
        return beanReference;
    }

    /**
     *
     * @param identifier
     * @return
     */
    public AbstractBeanReference findBeanReference(final String identifier) {
        return this.contextObjectsNameMap.get(identifier);
    }

    /**
     *
     *
     * @param parameterType
     * @param list
     * @return
     */
    Object createListElementArguments(final Class<?> parameterType, final ListType list)
            throws ContextInitializationException {
        // try to determine the type of the elements from the parameterType
        // for arrays this is dependable, for Collection it depends on compiler options
        Class<?> componentType = extractElementType(parameterType);

        List result = new ArrayList<>();

        for (Object argumentType : list.getBeanOrValueOrList()) {
            Object argValue = null;

            if (argumentType instanceof BeanType) {
                AbstractBeanReference dependency = createBeanReference((BeanType) argumentType);
                Object beanInstance = dependency.getBeanInstance();
                argValue = beanInstance;

            } else if (argumentType instanceof ListType) {
                Object listElements = createListElementArguments(String.class, (ListType) argumentType);
                argValue = listElements;

            } else if (argumentType instanceof ReferenceType) {
                AbstractBeanReference ref = createBeanReference((ReferenceType) argumentType);
                argValue = ref.getBeanInstance();

            } else {        // argumentType is String (static value)
                argValue = ContextUtility.createInstanceFromStringValue(componentType, argumentType.toString());
            }

            if (!componentType.isInstance(argValue)) {
                throw new InvalidElementTypeException(componentType, argValue);
            }

            result.add(argValue);
        }

        if (parameterType.isArray()) {
            return ContextUtility.createTypedArray(componentType, result);
        } else {
            return result;
        }
    }

    /**
     * Try to determine the type of the elements from the parameterType
     * For arrays this is dependable, for Collection it depends on compiler options.
     *
     * @param parameterType
     * @return
     * @throws UnknownCollectionTypeException
     */
    private Class<?> extractElementType(final Class<?> parameterType) throws UnknownCollectionTypeException {
        Class<?> componentType = Object.class;

        if (parameterType.isArray()) {
            componentType = parameterType.getComponentType();
        } else if (Collection.class.isInstance(parameterType)) {
            TypeVariable<? extends Class<?>>[] typeParameters = parameterType.getTypeParameters();
            if (typeParameters == null || typeParameters.length == 0) {
                componentType = Object.class;
            } else if (typeParameters.length > 1){
                throw new UnknownCollectionTypeException(parameterType);
            } else {
                componentType = typeParameters[0].getGenericDeclaration();
            }
        }
        return componentType;
    }

}
