package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.GenericContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidElementTypeException;
import com.paypal.credit.context.exceptions.UnknownCollectionTypeException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ReferenceType;

import java.lang.reflect.Array;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
        SimpleBeanReference beanReference = new SimpleBeanReference(this, reference);
        return beanReference;
    }

    /**
     *
     * @param identifier
     * @return
     */
    public AbstractBeanReference findBeanReference(final String identifier) {
        return this.beanReferences.get(identifier);
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
                argValue = argumentType.toString();
            }

            if (!componentType.isInstance(argValue)) {
                throw new InvalidElementTypeException(componentType, argValue);
            }

            result.add(argValue);
        }

        if (parameterType.isArray()) {
            Object arrayResult = Array.newInstance(componentType, result.size());
            int index = 0;
            for (Object element : result) {
                Array.set(arrayResult, index++, element);
            }
            return arrayResult;
        } else {
            return result;
        }
    }

}
