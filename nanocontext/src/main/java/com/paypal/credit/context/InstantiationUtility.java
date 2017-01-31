package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.UnknownCollectionTypeException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A collection of utility methods, usually used during context instantiation.
 * The following definitions are followed in variable naming in this code:
 * "parameter" - the definition within a method signature of the expected type
 * "argument" - the value passed to a method
 *
 * A parameter is part of the method signature,
 * the argument is what is passed at runtime.
 */
public class InstantiationUtility {
    /** Should the name of the valueOf methods ever change, we're ready */
    public static final String VALUE_OF_METHOD_NAME = "valueOf";

    /** used to find valueOf(String) methods */
    private final static Class<?>[] SINGLE_STRING_PARAMETER = new Class<?>[]{String.class};

    /** The packages in this array are treated specially when creating an instance from a String value */
    private final static Package[] CORE_PACKAGES = new Package[] {
            java.lang.Class.class.getPackage(),
    };

    /** Prevent instantiation */
    private InstantiationUtility() {}

    /**
     * Return true if the class is in the core (java.lang) package
     * @param clazz the class to evaluate
     * @return true if the class is in the core (java.lang) package, else false
     */
    public final static boolean isClassInCorePackage(final Class<?> clazz) {
        if (clazz != null) {
            Package clazzPackage = clazz.getPackage();
            for (Package corePackage : CORE_PACKAGES) {
                if (corePackage.equals(clazzPackage)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param clazz
     * @param value
     * @param <T>
     * @return
     * @throws CannotCreateObjectFromStringException
     */
    public static <T> T createInstanceFromStringValue(final Class<T> clazz, final String value, boolean coreClassesOnly)
            throws CannotCreateObjectFromStringException {
        Object[] parameters = new Object[]{value};

        // special handling for String target types
        if (String.class.equals(clazz)) {
            return (T)value;
        }

        // special handling for primitive types
        if (clazz.isPrimitive()) {
            if (byte.class.equals(clazz)) {
                return (T) (new Byte(value));
            } else if (short.class.equals(clazz)) {
                return (T) (new Short(value));
            } else if (int.class.equals(clazz)) {
                return (T) (new Integer(value));
            } else if (long.class.equals(clazz)) {
                return (T) (new Long(value));
            } else if (float.class.equals(clazz)) {
                return (T) (new Float(value));
            } else if (double.class.equals(clazz)) {
                return (T) (new Double(value));
            } else if (char.class.equals(clazz)) {
                return (T) new Character(value.charAt(0));
            }
        }

        // special handling for Class target types
        if (Class.class.equals(clazz)) {
            try {
                return (T)Class.forName(value);
            } catch (ClassNotFoundException x) {
                throw new CannotCreateObjectFromStringException(clazz, x);
            }
        }

        // special handling for core classes used for creating ConstantProperty
        // where it should not create a class of a type other than the core classes
        if (coreClassesOnly && ! isClassInCorePackage(clazz)) {
            throw new CannotCreateObjectFromStringException(clazz);
        }

        try {
            // look for a 'valueOf' method
            Method valueOfMethod = clazz.getMethod(VALUE_OF_METHOD_NAME, SINGLE_STRING_PARAMETER);
            return (T)valueOfMethod.invoke(null, parameters);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException x) {
            // look for a T(String) constructor
            try {
                Constructor ctor = clazz.getConstructor(String.class);
                return (T)ctor.newInstance(parameters);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new CannotCreateObjectFromStringException(clazz, x);
            }
        }
    }

    /**
     *
     * @param componentType
     * @param result
     * @return
     */
    public static Object createTypedArray(final Class<?> componentType, final List result) {
        Object arrayResult = Array.newInstance(componentType, result.size());
        int index = 0;
        for (Object element : result) {
            Array.set(arrayResult, index++, element);
        }
        return arrayResult;
    }

    /**
     * Try to determine the type of the elements from the parameterType
     * For arrays this is dependable, for Collection it depends on compiler options.
     *
     * @param parameterType
     * @return
     * @throws UnknownCollectionTypeException
     */
    public static Class<?> extractElementType(final Class<?> parameterType)
            throws UnknownCollectionTypeException {
        Class<?> componentType = null;

        if (parameterType.isArray() && !parameterType.getComponentType().isArray()) {
            // get the one and only element type
            componentType = parameterType.getComponentType();
        } else if (Collection.class.isAssignableFrom(parameterType)) {
            TypeVariable<? extends Class<?>>[] typeParameters = parameterType.getTypeParameters();
            if (typeParameters == null || typeParameters.length == 0) {
                // don't know, assume Object
                componentType = Object.class;
            } else if (typeParameters.length > 1){
                // more than one Type parameter, can't handle it !
                throw new UnknownCollectionTypeException(parameterType);
            } else {
                // get the one and only generic type declaration
                Type[] upperBounds = typeParameters[0].getBounds();
                String typeName = upperBounds[0].getTypeName();
                try {
                    componentType = Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    throw new UnknownCollectionTypeException(parameterType);
                }
            }
        }

        // will return null if the paremeterType is not a collection or array
        return componentType;
    }

    /**
     * Select the most specific constructor that will take the parameter types
     * @param beanClazz
     * @param orderedParameters
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> Constructor<T> selectConstructor(final Class<T> beanClazz, final List<AbstractProperty> orderedParameters)
            throws ContextInitializationException {
        SortedSet<Constructor<T>> sortedCtor =
                new TreeSet<Constructor<T>>(new ConstructorSpecificityComparator<T>());

        for (Constructor<?> ctor : beanClazz.getConstructors()) {
            if (isApplicableConstructor(ctor, orderedParameters)) {
                sortedCtor.add((Constructor<T>) ctor);
            }
        }

        return sortedCtor.size() > 0 ? sortedCtor.first() : null;
    }

    /**
     *
     * @param factoryClass
     * @param factoryMethodName
     * @param ctorParameters
     * @param valueType
     * @param <T>
     * @return
     * @throws ContextInitializationException
     */
    public static <T> Method selectStaticFactoryMethod(
            final Class<?> factoryClass,
            final String factoryMethodName,
            final List<AbstractProperty> ctorParameters,
            final Class<T> valueType) throws ContextInitializationException {
        return selectFactoryMethod(factoryClass, Modifier.PUBLIC + Modifier.STATIC, factoryMethodName, ctorParameters, valueType);
    }

    /**
     *
     * @param factory
     * @param factoryMethodName
     * @param ctorParameters
     * @param valueType
     * @param <T>
     * @return
     * @throws ContextInitializationException
     */
    public static <T> Method selectFactoryMethod(
            final Object factory,
            final String factoryMethodName,
            final List<AbstractProperty> ctorParameters,
            final Class<T> valueType) throws ContextInitializationException {
        return selectFactoryMethod(factory.getClass(), Modifier.PUBLIC, factoryMethodName, ctorParameters, valueType);
    }

    /**
     *
     * @param clazz
     * @param modifiers
     * @param methodName
     * @param orderedParameters
     * @param valueType
     * @param <T>
     * @return
     * @throws ContextInitializationException
     */
    private static <T> Method selectFactoryMethod(
            final Class<?> clazz,
            final int modifiers,
            final String methodName,
            final List<AbstractProperty> orderedParameters,
            final Class<T> valueType) throws ContextInitializationException {
        SortedSet<Method> sortedCtor =
                new TreeSet<Method>(new MethodSpecificityComparator());

        for (Method method : clazz.getMethods()) {
            if (methodName.equals(method.getName())
                    && isApplicableFactoryMethod(method, orderedParameters, valueType)
                    && (method.getModifiers() & modifiers) == modifiers) {
                sortedCtor.add((Method) method);
            }
        }

        return sortedCtor.size() > 0 ? sortedCtor.first() : null;
    }

    /**
     * Given a constructor and an ordered list of parameters (from the context XML),
     * determine whether the constructor could be called to instantiate the bean.
     *
     * @param ctor
     * @param parameters
     * @return
     */
    public static boolean isApplicableConstructor(final Constructor<?> ctor, final List<AbstractProperty> parameters)
            throws ContextInitializationException {
        // Iterate through each parameter in the given constructor
        // and determine whether the correlated ordered argument can be used in that parameter
        return isApplicableParameters(parameters, ctor.getParameterTypes());
    }

    /**
     * Given a method, an ordered list of parameters (from the context XML), and an expected return type,
     * determine whether the method could be called to instantiate the bean.
     *
     * @param method
     * @param parameters
     * @param returnType
     * @return
     */
    public static boolean isApplicableFactoryMethod(final Method method, final List<AbstractProperty> parameters, final Class<?> returnType)
            throws ContextInitializationException {
        if (method.getReturnType() == null && returnType == null
                || returnType.isAssignableFrom(method.getReturnType()) ) {

            // Iterate through each parameter in the given method
            // and determine whether the correlated ordered argument can be used in that parameter
            return isApplicableParameters(parameters, method.getParameterTypes());
        } else {
            return false;
        }
    }

    public static boolean isApplicableParameters(List<AbstractProperty> parameters, Class<?>[] parameterTypes) throws ContextInitializationException {
        if ( parameters == null && parameterTypes == null) {
            return true;
        }

        int parameterCount = parameters == null ? 0 : parameters.size();
        int parameterTypeCount = parameterTypes == null ? 0 : parameterTypes.length;

        if ( parameterCount != parameterTypeCount) {
            return false;
        }

        for (int index = 0; index < parameterTypes.length; ++index) {
            // Actual parameter type is the type of the current-index parameter
            // within the constructor.
            Class<?> constructorParameterType = parameterTypes[index];

            AbstractProperty parameter = parameters.get(index);

            if (!parameter.isResolvableAs(constructorParameterType)) {
                return false;
            }
        }

        return true;
    }

}
