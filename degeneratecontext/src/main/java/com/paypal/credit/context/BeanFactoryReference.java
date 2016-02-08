package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CircularReferenceException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.FailedToCreateCollectionException;
import com.paypal.credit.context.exceptions.FailedToInstantiateBeanException;
import com.paypal.credit.context.exceptions.NoApplicableConstructorException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The base class for bean Factory References, that is any type created from
 * a 'bean' element in the context definition XML.
 * NOTE: creating a BeanFactoryReference also creates references to
 * any beans referenced as constructor arguments in the context definition XML.
 */
abstract class BeanFactoryReference<T> extends AbstractBeanReference<T> {
    /** */
    private final BeanReferenceFactory beanReferenceFactory;

    /** */
    private final BeanType beanType;

    /** The set of references to other beans in the constructor args */
    private final Set<CtorArgReference<?>> ctorArgs = new HashSet<>();

    /**
     *
     * @param beanType
     * @throws ContextInitializationException
     */
    protected BeanFactoryReference(final BeanReferenceFactory beanReferenceFactory, final BeanType beanType)
            throws ContextInitializationException {
        super();
        this.beanReferenceFactory = beanReferenceFactory;
        this.beanType = beanType;
    }

    public BeanReferenceFactory getBeanReferenceFactory() {
        return beanReferenceFactory;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    protected T createBeanInstance() throws ContextInitializationException {
        List<ConstructorArgType> orderedParameters = ContextUtility.createOrderedParameterList(getBeanType());

        Constructor<?> ctor = ContextUtility.selectConstructor(getClazz(), orderedParameters);
        if (ctor != null) {
            Object[] parameters = createArguments(orderedParameters, ctor.getParameterTypes());

            try {
                return (T) ctor.newInstance(parameters);
            } catch (ClassCastException | InstantiationException  | IllegalAccessException  | InvocationTargetException x) {
                throw new FailedToInstantiateBeanException(getBeanType().getClazz(), x);
            }
        } else {
            throw new NoApplicableConstructorException(getClazz(), orderedParameters);
        }
    }


    /**
     *
     * @param orderedParameters
     * @param parameterTypes
     * @return
     * @throws ContextInitializationException
     */
    Object[] createArguments(final List<ConstructorArgType> orderedParameters, final Class<?>[] parameterTypes)
            throws ContextInitializationException {
        Object[] parameters = new Object[orderedParameters.size()];

        int index = 0;
        for (ConstructorArgType argument : orderedParameters) {
            if (argument.getBean() != null) {
                AbstractBeanReference dependency = getBeanReferenceFactory().createBeanReference(argument.getBean());
                Object beanInstance = dependency.getBeanInstance();
                parameters[index] = beanInstance;

            } else if (argument.getRef() != null) {
                AbstractBeanReference ref = getBeanReferenceFactory().createBeanReference(argument.getRef());
                parameters[index] = ref.getBeanInstance();

            } else if (argument.getValue() != null) {
                parameters[index] = ContextUtility.createInstanceFromStringValue(parameterTypes[index], argument.getValue());

            } else if (argument.getList() != null) {
                try {
                    Collection<Object> list = (Collection)parameterTypes[index].newInstance();
                    List listElements = ContextUtility.createListElementArguments(argument.getList(), Object.class);
                    for(Object listElement : listElements) {
                        list.add(listElement);
                    }
                    parameters[index] = list;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new FailedToCreateCollectionException(parameterTypes[index]);
                }

            }
            ++index;
        }
        return parameters;
    }

    /** Get the class of the referenced bean */
    public Class<T> getClazz() throws BeanClassNotFoundException {
        try {
            return (Class<T>)Class.forName(getClazzName());
        } catch (ClassCastException | ClassNotFoundException x) {
            throw new BeanClassNotFoundException(this.beanType.getClazz());
        }
    }

    public String getIdentifier() {
        return this.beanType.getId();
    }

    public String getClazzName() {
        return this.beanType.getClazz();
    }

    boolean addCtorArg(AbstractBeanReference beanReference, Integer index) {
        return ctorArgs.add(new CtorArgReference(beanReference, index));
    }

    public Set<CtorArgReference<?>> getCtorArgs() {
        return ctorArgs;
    }

    /**
     * A VO to contain references to beans that were references as constructor
     * arguments.
     *
     * @param <T> the bean type
     */
    protected class CtorArgReference<T> {
        private final AbstractBeanReference<T> beanReference;
        private final Integer ctorArgIndex;

        public CtorArgReference(final AbstractBeanReference<T> beanReference, final Integer ctorArgIndex) {
            this.beanReference = beanReference;
            this.ctorArgIndex = ctorArgIndex;
        }

        public AbstractBeanReference<T> getBeanReference() {
            return beanReference;
        }

        public Integer getCtorArgIndex() {
            return ctorArgIndex;
        }
    }

    // ===========================================================================================
    // Circular reference detection helpers
    // This should be looked at with respect to error handling,
    // as it could leave a ThreadLocal populated that shouldn't be.
    // ===========================================================================================

    /**
     * This method MUST be called by derived classes whenever the derived class
     * is instantiating a bean instance.
     *
     * @throws CircularReferenceException
     */
    protected void circularReferenceDetection() throws CircularReferenceException {
        Integer currentReference = System.identityHashCode(this);
        if (referenceExists(currentReference)) {
            clearReferences();
            throw new CircularReferenceException(getIdentifier(), getClazzName());
        } else {
            addReference(currentReference);
        }
    }

    // Thread local variable containing each threads current reference resolution set
    private static final ThreadLocal<Set<Integer>> currentReferences =
            new ThreadLocal<Set<Integer>>() {
                @Override protected Set<Integer> initialValue() {
                    return new HashSet<Integer>();
                }
            };

    // Returns the current thread's unique ID, assigning it if necessary
    private static Set<Integer> getCurrentReferences() {
        return currentReferences.get();
    }

    private static boolean referenceExists(final Integer currentReference) {
        return getCurrentReferences().contains(currentReference);
    }

    private static void addReference(final Integer currentReference) {
        getCurrentReferences().add(currentReference);
    }

    /**
     * This method MUST be called when a bean has been successfully created,
     * to prevent another thread from retaining the references and throwing a
     * spurious circular reference exception.
     */
    protected static void clearReferences() {
        getCurrentReferences().clear();
    }
}
