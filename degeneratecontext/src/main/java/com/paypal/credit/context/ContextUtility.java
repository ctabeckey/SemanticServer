package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ReferenceType;

import java.lang.reflect.Array;
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
                if (! isResolvableAs(actualArgType.getBean(), constructorParameterType)) {
                    return false;
                }
            } else if (actualArgType.getRef() != null) {
                if (! isResolvableAs(actualArgType.getRef(), constructorParameterType)) {
                    return false;
                }

            } else if (actualArgType.getList() != null) {
                if (! isResolvableAs(actualArgType.getList(), constructorParameterType)) {
                    return false;
                }

            } else if (actualArgType.getValue() != null) {
                if (! isResolvableAs((String)actualArgType.getValue(), constructorParameterType)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Return true if all of the elements in the beanOrValueOrList can resolve to an
     * instance of componentClazz.
     *
     * @param componentClazz - the class of the array elements
     * @param beanOrValueOrList - a list of beans, references, lists and/or values
     * @return true if the elements of the list can all be resolved as componentClazz instances
     */
    private static boolean isApplicableAsArray(final Class<?> componentClazz, final List<Object> beanOrValueOrList)
            throws BeanClassNotFoundException {
        for (Object listElement : beanOrValueOrList) {
            if (listElement instanceof BeanType) {
                BeanType beanType = (BeanType) listElement;

                if (! isResolvableAs(beanType, componentClazz)) {
                    return false;
                }
            } else if (listElement instanceof ReferenceType) {
                ReferenceType referenceType = (ReferenceType) listElement;

                if (! isResolvableAs(referenceType, componentClazz)) {
                    return false;
                }
            } else if (listElement instanceof ListType) {
                ListType listType = (ListType) listElement;

                if (! isResolvableAs(listType, componentClazz)) {
                    return false;
                }
            } else if (listElement instanceof String) {
                String valueType = (String) listElement;

                if (! isResolvableAs(valueType, componentClazz)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isResolvableAs(final BeanType beanType, final Class<?> clazz)
            throws BeanClassNotFoundException {
        Class<?> actualArgumentType = null;
        try {
            actualArgumentType = Class.forName(beanType.getClazz());
        } catch (ClassNotFoundException cnfX) {
            throw new BeanClassNotFoundException(beanType.getClazz());
        }
        if (!actualArgumentType.isAssignableFrom(clazz)) {
            return false;
        }

        return true;
    }

    private static boolean isResolvableAs(final ReferenceType referenceType, final Class<?> clazz) {
        referenceType.getBean();
        return true;
    }

    private static boolean isResolvableAs(final ListType listType, final Class<?> clazz)
            throws BeanClassNotFoundException {
        if (Collection.class.isAssignableFrom(clazz)) {
            TypeVariable<? extends Class<?>>[] actualTypeParameters = clazz.getTypeParameters();
            // TODO: validation here on the type parameters
            return true;
        } else if (clazz.isArray()){
            Class<?> componentClazz = clazz.getComponentType();
            if (isApplicableAsArray(componentClazz, listType.getBeanOrValueOrList())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean isResolvableAs(final String valueType, final Class<?> clazz) {
        try {
            Object parameterValue = createInstanceFromStringValue(clazz, valueType);
            if (!clazz.isInstance(parameterValue)) {
                return false;
            }
        } catch (CannotCreateObjectFromStringException e) {
            return false;
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

}
