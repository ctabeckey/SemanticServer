package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.GenericContextInitializationException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ReferenceType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A factory for all AbstractProperty derived classes.
 * A factory is attached to a Context because creating AbstractProperty instances
 * may require creating subordinate AbstractProperty instances, all of which
 * must belong to a single Context.
 *
 */
public final class PropertyFactory {
    private final Context context;

    /** prevent instantiation */
    PropertyFactory(final Context context) {
        this.context = context;
    }

    Context getContext() {
        return context;
    }

    /**
     *
     * @param ctorArgs
     * @return
     * @throws ContextInitializationException
     */
    List<AbstractProperty> create(final List<ConstructorArgType> ctorArgs)
            throws ContextInitializationException {
        List<AbstractProperty> properties = new ArrayList<>(ctorArgs == null ? 0 : ctorArgs.size());

        if (ctorArgs != null) {
            for (ConstructorArgType ctorArgType : ctorArgs) {
                AbstractProperty property = create(ctorArgType);
                properties.add(property);
            }
        }

        return properties;
    }

    /**
     *
     * @param prop
     * @return
     * @throws ContextInitializationException
     */
    AbstractProperty create(final ConstructorArgType prop)
            throws ContextInitializationException {

        if (prop.getBean() != null) {
            return createBeanInstanceFactory(prop.getBean());

        } else if (prop.getRef() != null) {
            return createReference(prop.getRef());

        } else if (prop.getValue() != null) {
            return createConstant(prop.getValue());

        } else if (prop.getList() != null) {
            return createList(prop.getList());
        }

        return null;
    }

    /**
     * Object createListElementArguments(final Class<?> parameterType, final ListType list)
     * @param list
     * @return
     */
    ListProperty createList(ListType list)
        throws ContextInitializationException {
        List<AbstractProperty> result = new ArrayList<>();

        for (Object argumentType : list.getBeanOrValueOrList()) {
            AbstractProperty property = null;

            if (argumentType instanceof BeanType) {
                property = createBeanInstanceFactory((BeanType) argumentType);

            } else if (argumentType instanceof ListType) {
                property = createList((ListType) argumentType);

            } else if (argumentType instanceof ReferenceType) {
                property = createReference((ReferenceType) argumentType);

            } else if (argumentType instanceof String){        // argumentType is String (static value)
                property = createConstant((String)argumentType);
            }

            result.add(property);
        }

        return new ListProperty(this.getContext(), result);
    }

    /**
     *
     * @param value
     * @return
     */
    ConstantProperty createConstant(String value) {
        return new ConstantProperty(this.getContext(), value, String.class);
    }

    /**
     * Create a reference to another Bean
     *
     * @param ref
     * @return
     * @throws ContextInitializationException
     */
    private BeanReference createReference(ReferenceType ref)
            throws ContextInitializationException {
        return new BeanReference(this.getContext(), ref.getBean());
    }

    /**
     *
     * @param beanType
     * @return
     * @throws ContextInitializationException
     */
    AbstractBeanInstanceFactory createBeanInstanceFactory(final BeanType beanType)
            throws ContextInitializationException {
        AbstractBeanInstanceFactory beanReference = null;

        switch(beanType.getScope()) {
            case PROTOTYPE: {
                List<AbstractProperty> ctorArgs = null;
                if (beanType.getConstructorArg() != null && beanType.getConstructorArg().size() > 0) {
                    ctorArgs = create(createOrderedParameterList(beanType));
                }
                beanReference = new PrototypeBeanInstanceFactory(context, beanType.getId(), beanType.getArtifact(), beanType.getClazz(), ctorArgs);
                break;
            }
            case SINGLETON: {
                List<AbstractProperty> ctorArgs = null;
                if (beanType.getConstructorArg() != null && beanType.getConstructorArg().size() > 0) {
                    ctorArgs = create(createOrderedParameterList(beanType));
                }
                beanReference = new SingletonBeanInstanceFactory(context, beanType.getId(), beanType.getArtifact(), beanType.getClazz(), ctorArgs);
                break;
            }
            default: {
                throw new GenericContextInitializationException(
                        String.format("Unrecognized scope specifier (%s) in context definition for bean %s", beanType.getScope(), beanType.getId())
                );
            }
        }

        return beanReference;
    }

    /**
     * Create a List of ConstructorArgType instances from the constructor argument
     * declarations. The List will be ordered based on the "index" properties and
     * the ordering within the original XML.
     * ConstructorArgType with an index are first placed into the assigned locations and
     * then the un-indexed ConstructorArgType are added, first to any empty index left
     * by the indexed ConstructorArgType and then at the end.
     * Any remaining empty slots left in the List will result in an exception.
     * e.g.
     * ConstructorArgType with indexes 1 and 3, and no other ConstructorArgType will
     * result in an exception because slot 2 is empty.
     * ConstructorArgType with indexes 1 and 3, and one more un-indexed ConstructorArgType
     * will NOT result in an exception. The un-indexed ConstructorArgType will occupy slot 2.
     *
     * @param beanType
     * @return
     */
    public List<ConstructorArgType> createOrderedParameterList(final BeanType beanType)
            throws ContextInitializationException {
        List<ConstructorArgType> orderedParameterTypes = new ArrayList<>();

        // first put the args with an index where they want to be
        for (ConstructorArgType ctorArgType : beanType.getConstructorArg()) {
            Integer index = ctorArgType.getIndex();
            if (index != null) {
                int targetIndex = index.intValue();
                while(orderedParameterTypes.size() < targetIndex) {
                    orderedParameterTypes.add(null);
                }
                orderedParameterTypes.add(targetIndex, ctorArgType);
            }
        }
        // then put the args without an index into the left over spots
        int index = 0;
        for (ConstructorArgType ctorArgType : beanType.getConstructorArg()) {
            if (ctorArgType.getIndex() == null) {
                // find the next empty spot, or the end of the list
                while(orderedParameterTypes.size() != 0
                        && index < orderedParameterTypes.size()
                        && orderedParameterTypes.get(index) != null) {
                    ++index;
                }
                if (index >= orderedParameterTypes.size()) {
                    orderedParameterTypes.add(index, ctorArgType);       // add at the end
                } else {
                    orderedParameterTypes.set(index, ctorArgType);       // replace the null entry with the real entry
                }
            }
        }

        // validate that the List of arguments has no nulls left in it
        for(ConstructorArgType argType : orderedParameterTypes) {
            if (argType == null) {
                throw new SparseArgumentListDetectedException(beanType);
            }
        }

        return orderedParameterTypes;
    }

    /**
     * Create a PreresolvedBean instance for externally defined beans
     *
     * @param xBeanDef an identifier and a bean
     * @return a PreresolvedBean instance
     */
    public AbstractReferencableProperty create(ContextFactory.ExternalBeanDefinition xBeanDef)
            throws ContextInitializationException {
        String id = xBeanDef.getIdentifier();
        id = id == null ? UUID.randomUUID().toString() : id;    // assure that an ID exists

        return new PreresolvedBean(getContext(), id, xBeanDef.getBeanInstance());
    }
}
