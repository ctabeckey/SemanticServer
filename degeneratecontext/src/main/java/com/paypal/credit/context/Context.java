package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.FailedToCreateCollectionException;
import com.paypal.credit.context.exceptions.FailedToInstantiateBeanException;
import com.paypal.credit.context.exceptions.NoApplicableConstructorException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.utility.references.Derivations;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
    private final Map<String, Object> contextObjectsNameMap = new HashMap<>();

    private STATE state = STATE.NEW;

    private Context(final BeansType beans) {
        this.beans = beans;
    }

    /**
     *
     */
    private void initialize() throws ContextInitializationException {
        this.state = STATE.INITIALIZING;
        Set<BeanTypeDecorator> decorators = createBeanDecoratorHierarchy(beans.getBean());

        for (BeanTypeDecorator decorator : decorators) {
            Object bean = createBeanHierarchy(decorator.getBeanType());
            String id = decorator.getBeanType().getId() == null ?
                    UUID.randomUUID().toString() :
                    decorator.getBeanType().getId();
            contextObjectsNameMap.put( id, bean);
        }

        this.state = STATE.INITIALIZED;
    }

    /**
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final Class<T> beanClass) {
        if (state != STATE.INITIALIZED) {
            throw new IllegalStateException(
                    String.format("Context is not initialized, current state is %s", state)
            );
        }

        int minDistance = Integer.MAX_VALUE;
        Object selectedBean = null;
        for (Object bean : contextObjectsNameMap.values()) {
            int beanDistance = Derivations.instanceDistance(bean, beanClass);
            if (beanDistance < minDistance) {
                minDistance = beanDistance;
                selectedBean = bean;
            }
        }

        return (T)selectedBean;
    }

    /**
     *
     * @param name
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T getBean(final String name, final Class<T> beanClass) {
        if (state != STATE.INITIALIZED) {
            throw new IllegalStateException(
                    String.format("Context is not initialized, current state is %s", state)
            );
        }

        Object bean = contextObjectsNameMap.get(name);
        if (beanClass != null && beanClass.isInstance(bean)) {
            return (T)bean;
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

    /**
     *
     * @param contextDefinition
     * @return
     * @throws JAXBException
     * @throws ContextInitializationException
     * @throws FileNotFoundException
     */
    public final static Context create(final File contextDefinition)
            throws JAXBException, ContextInitializationException, FileNotFoundException {
        FileInputStream fiS = new FileInputStream(contextDefinition);
        try {
            return create(fiS);
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
    public final static Context create(final URL contextDefinition)
            throws JAXBException, ContextInitializationException, IOException {
        InputStream urlIS = contextDefinition.openStream();
        try {
            return create(urlIS);
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
    public final static Context create(final InputStream inputStream)
            throws JAXBException, ContextInitializationException {
        JAXBContext jaxbContext = getJaxbContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<BeansType> ctx = (JAXBElement<BeansType>) unmarshaller.unmarshal(inputStream);
        return create(ctx.getValue());
    }

    /**
     *
     * @param beansType
     * @return
     * @throws ContextInitializationException
     */
    public final static Context create(final BeansType beansType)
            throws ContextInitializationException {
        if (beansType == null) {
            throw new IllegalArgumentException("BeansType is null and must not be.");
        }

        Context ctx = new Context(beansType);
        ctx.initialize();

        return ctx;
    }

    /**
     *
     * @return
     */
    static Set<BeanTypeDecorator> createBeanDecoratorHierarchy(List<BeanType> beanTypes) {
        // will contain a Set of the top-level (i.e. those with no dependents) BeanDecorators
        // dependencies are children of the top level decorators
        Set<BeanTypeDecorator> decorators = new HashSet<>();

        for (BeanType beanType : beanTypes) {
            decorators.add(new BeanTypeDecorator(beanType));
        }

        return decorators;
    }

    /**
     * This method both creates the beans in the hierarchy beneath a top-level
     * BeanDecorator and also adds the beans to the context list.
     * @param beanType
     */
    static Object createBeanHierarchy(final BeanType beanType)
            throws ContextInitializationException {

        Class<?> beanClazz = null;
        try {
            beanClazz = Class.forName(beanType.getClazz());
        } catch (ClassNotFoundException cnfX) {
            throw new BeanClassNotFoundException(beanType.getClazz());
        }
        List<ConstructorArgType> orderedParameters = createOrderedParameterList(beanType);

        Constructor<?> ctor = selectConstructor(beanClazz, orderedParameters);
        if (ctor != null) {
            Object[] parameters = createArguments(orderedParameters, ctor.getParameterTypes());

            try {
                return ctor.newInstance(parameters);
            } catch (InstantiationException  | IllegalAccessException  | InvocationTargetException x) {
                throw new FailedToInstantiateBeanException(beanType.getClazz(), x);
            }
        } else {
            throw new NoApplicableConstructorException(beanClazz, orderedParameters);
        }
    }

    /**
     *
     * @param orderedParameters
     * @param parameterTypes
     * @return
     * @throws ContextInitializationException
     */
    static Object[] createArguments(final List<ConstructorArgType> orderedParameters, final Class<?>[] parameterTypes)
            throws ContextInitializationException {
        Object[] parameters = new Object[orderedParameters.size()];

        int index = 0;
        for (ConstructorArgType argument : orderedParameters) {
            if (argument.getBean() != null) {
                Object dependency = createBeanHierarchy(argument.getBean());
                parameters[index] = dependency;
            } else if (argument.getValue() != null) {
                parameters[index] = createInstanceFromStringValue(parameterTypes[index], argument.getValue());
            } else if (argument.getList() != null) {
                try {
                    Collection<Object> list = (Collection)parameterTypes[index].newInstance();
                    List listElements = createListElementArguments(argument.getList(), Object.class);
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

    /**
     *
     * @param list
     * @param elementType
     * @return
     */
    static <T> List<T> createListElementArguments(final ListType list, final Class<T> elementType) {
        List<T> result = new ArrayList<T>();

        int index = 0;
        for (Object argument : list.getBeanOrValueOrList()) {
            if (argument instanceof BeanType) {

            } else if (argument instanceof ListType) {

            } else if (argument instanceof String) {

            } else {
                // WTF!
            }
            ++index;
        }

        return result;
    }


    /**
     *
     * @param beanType
     * @return
     */
    static List<ConstructorArgType> createOrderedParameterList(final BeanType beanType)
        throws SparseArgumentListDetectedException {
        List<ConstructorArgType> orderedArgumentTypes = new ArrayList<>();

        // first put the args with an index where they want to be
        for (ConstructorArgType ctorArgType : beanType.getConstructorArg()) {
            Integer index = ctorArgType.getIndex();
            if (index != null) {
                int targetIndex = index.intValue();
                while(orderedArgumentTypes.size() < targetIndex) {
                    orderedArgumentTypes.add(null);
                }
                orderedArgumentTypes.add(targetIndex, ctorArgType);
            }
        }
        // then put the args without an index into the left over spots
        int index = 0;
        for (ConstructorArgType ctorArgType : beanType.getConstructorArg()) {
            if (ctorArgType.getIndex() == null) {
                // find the next empty spot, or the end of the list
                while(orderedArgumentTypes.size() != 0
                        && index < orderedArgumentTypes.size()
                        && orderedArgumentTypes.get(index) != null) {
                    ++index;
                }
                if (index >= orderedArgumentTypes.size()) {
                    orderedArgumentTypes.add(index, ctorArgType);       // add at the end
                } else {
                    orderedArgumentTypes.set(index, ctorArgType);       // replace the null entry with the real entry
                }
            }
        }

        // validate that the List of arguments has no nulls left in it
        for(ConstructorArgType argType : orderedArgumentTypes) {
            if (argType == null) {
                throw new SparseArgumentListDetectedException(beanType);
            }
        }

        return orderedArgumentTypes;
    }

    /**
     * Select the most specific constructor that will take the parameter types
     * @param beanClazz
     * @param orderedParameters
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    static <T> Constructor<T> selectConstructor(final Class<T> beanClazz, final List<ConstructorArgType> orderedParameters)
            throws BeanClassNotFoundException {
        SortedSet<Constructor<T>> sortedCtor = new TreeSet<>(new Comparator<Constructor<?>>(){
            @Override
            public int compare(final Constructor<?> ctor1, final Constructor<?> ctor2) {
                if (ctor1.getParameterTypes().length < ctor2.getParameterTypes().length) {
                    return -1;
                } else if (ctor1.getParameterTypes().length < ctor2.getParameterTypes().length) {
                    return 1;
                }
                for (int index = 0; index < ctor1.getParameterTypes().length; ++index) {
                    if (ctor1.getParameterTypes()[index].equals(ctor2.getParameterTypes()[index])) {
                        continue;
                    } else if (ctor1.getParameterTypes()[index].isAssignableFrom(ctor2.getParameterTypes()[index])) {
                        return 2;
                    } else if (ctor2.getParameterTypes()[index].isAssignableFrom(ctor1.getParameterTypes()[index])) {
                        return -2;
                    }
                }
                return 0;
            }
        });

        for (Constructor<?> ctor : beanClazz.getConstructors()) {
            if (isApplicableConstructor(ctor, orderedParameters)) {
                sortedCtor.add((Constructor<T>) ctor);
            }
        }

        return sortedCtor.size() > 0 ? sortedCtor.first() : null;
    }

    /**
     *
     * @param ctor
     * @param orderedParameters
     * @return
     */
    static boolean isApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedParameters)
            throws BeanClassNotFoundException {
        if (ctor.getParameterTypes().length != (orderedParameters == null ? 0 : orderedParameters.size())) {
            return false;
        }

        for (int index = 0; index < ctor.getParameterTypes().length; ++index) {
            Class<?> actualParameterType = ctor.getParameterTypes()[index];

            ConstructorArgType argType = orderedParameters.get(index);
            if (argType.getBean() != null) {
                Class<?> requiredParameterType = null;
                try {
                    requiredParameterType = Class.forName(argType.getBean().getClazz());
                } catch (ClassNotFoundException cnfX) {
                    throw new BeanClassNotFoundException(argType.getBean().getClazz());
                }
                if (!requiredParameterType.isAssignableFrom(actualParameterType)) {
                    return false;
                }
            } else if (argType.getList() != null) {
                if (Collection.class.isAssignableFrom(actualParameterType)) {
                    TypeVariable<? extends Class<?>>[] actualTypeParameters = actualParameterType.getTypeParameters();
                    // should do some validation here on the type parameters, but this is a start
                    return true;
                } else {
                    return false;
                }
            } else if (argType.getValue() != null) {
                try {
                    createInstanceFromStringValue(actualParameterType, argType.getValue());
                } catch (CannotCreateObjectFromStringException e) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     *
     * @param clazz
     * @param value
     * @param <T>
     * @return
     * @throws CannotCreateObjectFromStringException
     */
    private final static Class<?>[] SINGLE_STRING_PARAMETER = new Class<?>[]{String.class};
    public static <T> T createInstanceFromStringValue(final Class<T> clazz, final String value)
            throws CannotCreateObjectFromStringException {
        Object[] parameters = new Object[]{value};

        try {
            try {
                // look for a 'valueOf' method
                Method valueOfMethod = clazz.getMethod("valueOf", SINGLE_STRING_PARAMETER);
                return (T)valueOfMethod.invoke(null, parameters);
            } catch (NoSuchMethodException e) {
                // ignore the exception and look for a constructor
                Constructor<?> ctor = clazz.getDeclaredConstructor(SINGLE_STRING_PARAMETER);
                return (T)ctor.newInstance(parameters);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException x) {
            throw new CannotCreateObjectFromStringException(clazz, x);
        }
    }

    /**
     *
     */
    static class BeanTypeDecorator {
        private final BeanType beanType;
        private final Set<BeanTypeDecorator> dependencies = new HashSet<>();

        BeanTypeDecorator(final BeanType beanType) {
            this.beanType = beanType;

            for(ConstructorArgType ctorArg : beanType.getConstructorArg()) {
                BeanType dependency = ctorArg.getBean();
                if (dependency != null) {
                    BeanTypeDecorator dependencyDecorator = new BeanTypeDecorator(dependency);
                    dependencies.add(dependencyDecorator);
                }
            }
        }

        BeanType getBeanType() {
            return this.beanType;
        }

        Set<BeanTypeDecorator> getDependencies() {
            return this.dependencies;
        }
    }
}
