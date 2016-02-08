package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A collection of utility methods, usually used during context instantiation.
 */
public class ContextUtility {
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

                    } else if (String.class.equals(ctor1.getParameterTypes()[index])
                        && !String.class.equals(ctor2.getParameterTypes()[index])) {
                        // one of two odd cases where a constructor may take a type that can be constructed
                        // using a valueOf(String) method. Constructors taking a String should be sorted later
                        // than an otherwise equivalent constructor (taking an Integer for instance)
                        return 3;
                    } else if (String.class.equals(ctor2.getParameterTypes()[index])
                            && !String.class.equals(ctor1.getParameterTypes()[index])) {
                        return -3;
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
     * Given a constructor and an ordered list of parameters (from the context XML),
     * determine whether the constructor could be called to instantiate the bean.
     *
     * @param ctor
     * @param orderedArguments
     * @return
     */
    static boolean isApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedArguments)
            throws BeanClassNotFoundException {
        if (ctor.getParameterTypes().length != (orderedArguments == null ? 0 : orderedArguments.size())) {
            return false;
        }

        // Iterate through each parameter in the given constructor
        // and determine whether the correlated ordered argument can be used in that parameter
        for (int index = 0; index < ctor.getParameterTypes().length; ++index) {
            // Actual parameter type is the type of the current-index parameter
            // within the constructor.
            Class<?> constructorParameterType = ctor.getParameterTypes()[index];

            ConstructorArgType actualArgType = orderedArguments.get(index);
            if (actualArgType.getBean() != null) {
                Class<?> actualArgumentType = null;
                try {
                    actualArgumentType = Class.forName(actualArgType.getBean().getClazz());
                } catch (ClassNotFoundException cnfX) {
                    throw new BeanClassNotFoundException(actualArgType.getBean().getClazz());
                }
                if (!actualArgumentType.isAssignableFrom(constructorParameterType)) {
                    return false;
                }
            } else if (actualArgType.getList() != null) {
                if (Collection.class.isAssignableFrom(constructorParameterType)) {
                    TypeVariable<? extends Class<?>>[] actualTypeParameters = constructorParameterType.getTypeParameters();
                    // should do some validation here on the type parameters, but this is a start
                    continue;
                } else {
                    return false;
                }
            } else if (actualArgType.getValue() != null) {
                try {
                    Object parameterValue = createInstanceFromStringValue(constructorParameterType, actualArgType.getValue());
                    if (!constructorParameterType.isInstance(parameterValue)) {
                        return false;
                    }
                    continue;
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

        if (String.class.equals(clazz)) {
            return (T)value;
        }

        try {
            // look for a 'valueOf' method
            Method valueOfMethod = clazz.getMethod("valueOf", SINGLE_STRING_PARAMETER);
            return (T)valueOfMethod.invoke(null, parameters);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException x) {
            throw new CannotCreateObjectFromStringException(clazz, x);
        }
    }
}
