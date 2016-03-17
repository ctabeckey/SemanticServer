package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.FailedToInstantiateBeanException;
import com.paypal.credit.context.exceptions.NoApplicableConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The base class for bean Factory References, that is any type created from
 * a 'bean' element in the context definition XML.
 * NOTE: creating a BeanFactoryReference also creates references to
 * any beans referenced as constructor arguments in the context definition XML.
 *
 * @see com.paypal.credit.context.AbstractProperty
 *
 * Known Derivations
 * @see PrototypeBeanInstanceFactory
 * @see PrototypeBeanInstanceFactory
 */
abstract class AbstractBeanInstanceFactory<T>
        extends AbstractReferencableProperty<T> {
    private final String artifactIdentifier;
    private final String clazzName;
    private final List<AbstractProperty> ctorParameters;

    /** */
    private final ReentrantLock beanClassLock = new ReentrantLock();

    /** */
    private Class<T> beanClass;
    private Set<AbstractProperty> constructorParameterProperties;

    /**
     *
     * @param id
     * @param artifactIdentifier
     * @param clazzName
     * @param ctorParameters
     * @throws ContextInitializationException
     */
    protected AbstractBeanInstanceFactory(
            final Context context,
            final String id,
            final String artifactIdentifier,
            final String clazzName,
            final List<AbstractProperty> ctorParameters
    ) throws ContextInitializationException {
        super(context, id);
        this.artifactIdentifier = artifactIdentifier;
        this.clazzName = clazzName;
        this.ctorParameters = ctorParameters;
    }

    public String getClazzName() {
        return this.clazzName;
    }

    /**
     *
     * @return
     */
    public List<AbstractProperty> getConstructorParameterProperties() {
        return ctorParameters;
    }

    /**
     *
     * @return
     */
    private URLClassLoader getArtifactClassLoader() {
        if (artifactIdentifier != null) {
            ArtifactHolder holder = getContext().getArtifactHolder(artifactIdentifier);
            return holder.getClassLoader();
        }
        return null;
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    protected T createBeanInstance() throws ContextInitializationException {
        //getContextFactory().build();
        Constructor<?> ctor = InstantiationUtility.selectConstructor(getValueType(), ctorParameters);
        if (ctor != null) {
            Object[] parameters = createArguments(ctorParameters, ctor.getParameterTypes());

            try {
                //detectCircularReferences(this);
                T instance = (T) ctor.newInstance(parameters);
                //clearReferences();

                return instance;
            } catch (ClassCastException | InstantiationException  | IllegalAccessException  | InvocationTargetException x) {
                throw new FailedToInstantiateBeanException(clazzName, x);
            }
        } else {
            throw new NoApplicableConstructorException(getValueType(), ctorParameters);
        }
    }

    /**
     *
     * @param orderedParameters
     * @param parameterTypes
     * @return
     * @throws ContextInitializationException
     */
    Object[] createArguments(final List<AbstractProperty> orderedParameters, final Class<?>[] parameterTypes)
            throws ContextInitializationException {

        if (orderedParameters == null || orderedParameters.size() == 0) {
            return new Object[0];
        }

        Object[] parameters = new Object[orderedParameters.size()];

        int index = 0;
        for (AbstractProperty argument : orderedParameters) {
            Class<?> parameterType = parameterTypes[index];

            AbstractProperty morphedProperty = argument.morph(parameterType);

            parameters[index] = morphedProperty.getValue();

            ++index;
        }
        return parameters;
    }

    /** Get the class of the referenced bean */
    public Class<T> getValueType() throws BeanClassNotFoundException {
        beanClassLock.lock();
        try {
            if (beanClass == null) {
                ClassLoader artifactClassLoader = getArtifactClassLoader();

                try {
                    if (artifactClassLoader != null) {
                        beanClass = (Class<T>) artifactClassLoader.loadClass(getClazzName());
                    } else {
                        beanClass = (Class<T>) Class.forName(getClazzName());
                    }
                } catch (ClassCastException | ClassNotFoundException x) {
                    throw new BeanClassNotFoundException(getClazzName(), x);
                }
            }
        } finally {
            beanClassLock.unlock();
        }

        return beanClass;
    }

    /**
     * Determine if the instance that will be (or has been) created from the BeanType
     * is assignable to a reference to the given class.
     *
     *
     * @param clazz the Class of a parameter for which the beanType resultant will be assigned
     * @return true, if an assignment of beanType resultant to a refernce of type clazz would succeed.
     * @throws BeanClassNotFoundException
     */
    @Override
    public boolean isResolvableAs(Class<?> clazz) throws ContextInitializationException {
        Class<?> actualArgumentType = getValueType();
        if (!clazz.isAssignableFrom(actualArgumentType)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "BeanFactoryReference{" +
                "id='" + getIdentifier() + '\'' +
                ", clazzName='" + getClazzName() + '\'' +
                '}';
    }

}
