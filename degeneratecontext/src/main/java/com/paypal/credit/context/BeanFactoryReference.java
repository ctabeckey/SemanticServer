package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CircularReferenceException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.FailedToInstantiateBeanException;
import com.paypal.credit.context.exceptions.NoApplicableConstructorException;
import com.paypal.credit.context.xml.ArtifactType;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The base class for bean Factory References, that is any type created from
 * a 'bean' element in the context definition XML.
 * NOTE: creating a BeanFactoryReference also creates references to
 * any beans referenced as constructor arguments in the context definition XML.
 *
 * @see com.paypal.credit.context.AbstractBeanReference
 *
 * Known Derivations
 * @see com.paypal.credit.context.PrototypeBeanFactoryReference
 * @see com.paypal.credit.context.PrototypeBeanFactoryReference
 */
abstract class BeanFactoryReference<T> extends AbstractBeanReference<T> {
    /** */
    private final ContextFactory contextFactory;

    /** */
    private final BeanType beanType;

    /** */
    private final Object beanClassMonitor = new Object();
    /** */
    private Class<T> beanClass;

    /**
     *
     * @param beanType
     * @throws ContextInitializationException
     */
    protected BeanFactoryReference(final ContextFactory contextFactory, final BeanType beanType)
            throws ContextInitializationException {
        super();
        this.contextFactory = contextFactory;
        this.beanType = beanType;
    }

    public ContextFactory getContextFactory() {
        return contextFactory;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    /**
     *
     * @return
     */
    private URLClassLoader getArtifactClassLoader() {
        // get the identifier of the Artifact
        String artifactReference = getArtifactReference();
        if (artifactReference != null && artifactReference.length() > 0) {
            // get the ArtifactType (maps the artifact reference to the artifact URI)
            ContextFactory.ArtifactHolder holder = getContextFactory().getArtifactType(artifactReference);
            // if there is a Holder for the artifact reference (created previously) then get its class loader
            return holder == null ? null : holder.getClassLoader();
        }
        return null;
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
                AbstractBeanReference dependency = getContextFactory().createBeanReference(argument.getBean());
                Object beanInstance = dependency.getBeanInstance();
                parameters[index] = beanInstance;

            } else if (argument.getRef() != null) {
                AbstractBeanReference ref = getContextFactory().createBeanReference(argument.getRef());
                parameters[index] = ref.getBeanInstance();

            } else if (argument.getValue() != null) {
                parameters[index] = ContextUtility.createInstanceFromStringValue(parameterTypes[index], argument.getValue());

            } else if (argument.getList() != null) {
                Object listElements =
                        getContextFactory().createListElementArguments(parameterTypes[index], argument.getList());
                parameters[index] = listElements;

            }
            ++index;
        }
        return parameters;
    }

    /** Get the class of the referenced bean */
    public Class<T> getClazz() throws BeanClassNotFoundException {
        synchronized (beanClassMonitor) {
            if (beanClass == null) {
                ClassLoader artifactClassLoader = getArtifactClassLoader();

                try {
                    if (artifactClassLoader != null) {
                        beanClass = (Class<T>) artifactClassLoader.loadClass(getClazzName());
                    } else {
                        beanClass = (Class<T>) Class.forName(getClazzName());
                    }
                } catch (ClassCastException | ClassNotFoundException x) {
                    throw new BeanClassNotFoundException(this.beanType.getClazz(), x);
                }
            }
        }

        return beanClass;
    }

    public String getIdentifier() {
        return this.beanType.getId();
    }

    public String getClazzName() {
        return this.beanType.getClazz();
    }

    public String getArtifactReference() {
        return this.beanType.getArtifact();
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
