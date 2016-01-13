package com.paypal.credit;

import com.paypal.credit.processors.ProcessorInfo;
import com.paypal.credit.processors.exceptions.NoApplicableConstructorException;
import com.paypal.credit.processors.exceptions.ProcessorInstantiationException;

import javax.validation.constraints.NotNull;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A collection of utility methods to do fancy reflection and Bean stuff
 */
public final class ReflectionUtilities {
    /** A private constructor to prevent instantiation */
    private ReflectionUtilities() { }

    /**
     *
     * @param parameterTypes
     * @param settings
     * @return
     */
    public final static Object[] createArgumentsArray(final Class<?>[] parameterTypes, final Object settings)
    throws IntrospectionException {
        BeanInfo settingsInfo = Introspector.getBeanInfo(settings.getClass());

        PropertyDescriptor[] mappedProperties = mapPropertyDescriptors(parameterTypes, settingsInfo);
        if (mappedProperties == null)
            return null;

        Object[] result = new Object[mappedProperties.length];
        for (int index = 0; index < mappedProperties.length; ++index) {
            try {
                result[index] = mappedProperties[index].getReadMethod().invoke(settings, null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        return result;
    }

    /**
     * Determine how to map from the settingsInfo into the parameterTypes.
     * Practically speaking, how to map Bean properties into the parameters of a constructor or method.
     *
     * @param parameterTypes an array of parameter types (usually from a Constructor or a Method instance)
     * @param settingsInfo a BeanInfo instance describing a class from which parameters are to be drawn
     * @return an array of PropertyDescriptor whose length matches parameterTypes and for which each
     *         PropertyDescriptor describes a property from the BeanInfo from where a value can be
     *         drawn that will satisfy the required, corresponding, parameter type.
     */
    private static PropertyDescriptor[] mapPropertyDescriptors(final Class<?>[] parameterTypes, final BeanInfo settingsInfo) {
        // the settings properties that will be mapped to the constructor in order
        PropertyDescriptor[] mappedProperties = new PropertyDescriptor[parameterTypes.length];
        for (int index = 0; index < mappedProperties.length; ++index) {
            Class<?> parameterType = parameterTypes[index];
            mappedProperties[index] = findSettingsProperty(parameterType, settingsInfo);
        }

        // validate that the constructor will have all of its parameters mapped
        // and that none are mapped twice
        for (int index = 0; index < mappedProperties.length; ++index) {
            if (mappedProperties[index] == null) {
                return null;
            } else {
                for(int jindex = index + 1; jindex < mappedProperties.length; ++jindex) {
                    if (mappedProperties[index].equals(mappedProperties[jindex])) {
                        return null;
                    }
                }
            }
        }

        return mappedProperties;
    }

    /**
     *
     * @param parameterType
     * @param settingsInfo
     * @return
     */
    public static final PropertyDescriptor findSettingsProperty(final Class<?> parameterType, final BeanInfo settingsInfo) {
        SortedSet<PropertyDescriptor> candidates = new TreeSet<PropertyDescriptor>(new Comparator<PropertyDescriptor>() {
            @Override
            public int compare(final PropertyDescriptor propDesc1, final PropertyDescriptor propDesc2) {
                return degreesOfSeparation(propDesc1.getPropertyType(), propDesc2.getPropertyType());
            }
        });

        for (PropertyDescriptor propertyDesc : settingsInfo.getPropertyDescriptors()) {
            if (parameterType.isAssignableFrom(propertyDesc.getPropertyType())) {
                candidates.add(propertyDesc);
            }
        }

        if (candidates.size() == 0) {
            // unable to find a setting of a compatible type
            return null;
        }

        // return the most specific setting by type
        return candidates.first();
    }

    /**
     *
     * @param processorClass
     * @param settings
     * @return
     * @throws NoApplicableConstructorException
     * @throws ProcessorInstantiationException
     */
    public static <T> T createInstanceFromSettings(final @NotNull Class<T> processorClass, final @NotNull Object settings)
            throws NoApplicableConstructorException, ProcessorInstantiationException, IntrospectionException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassCastException {
        Constructor ctor = findFirstApplicableConstructor(processorClass, settings);
        if (ctor == null) {
            throw new NoApplicableConstructorException(processorClass, settings);
        }

        Object[] ctorArguments = createArgumentsArray(ctor.getParameterTypes(), settings);

        return (T) ctor.newInstance(ctorArguments);
    }

    /**
     *
     * @param clazz
     * @param settings
     * @return
     */
    public static final Constructor findFirstApplicableConstructor(final Class<?> clazz, final Object settings) {
        BeanInfo settingsInfo = null;

        try {
            settingsInfo = Introspector.getBeanInfo(settings.getClass());
            return findFirstApplicableConstructor(clazz, settingsInfo);
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param clazz
     * @param settingsInfo
     * @return
     */
    public static final Constructor findFirstApplicableConstructor(final Class<?> clazz, final BeanInfo settingsInfo) {
        for (Constructor ctor : clazz.getConstructors()) {
            if (mapPropertyDescriptors(ctor.getParameterTypes(), settingsInfo) != null) {
                // returns the first constructor it finds that will work
                return ctor;
            }
        }
        return null;
    }

    /**
     * Returns sum of the degree of relationship of the base to the derivative, guaranteeing
     * that base[n].isAssignableFrom(derivative[n]) is always true.
     *
     * 0 - all of the corresponding classes are equal
     * negative - the levels of ancestry (narrowing) from base to derivative
     * Integer.MAX_VALUE - the derivative array cannot be assigned to the base array
     */
     public static final int degreesOfSeparation(final Class<?>[] base, final Class<?>[] derivative) {
        if (base == null && derivative == null) {
            return 0;
        }
        if (base == null && derivative != null
                || base != null && derivative == null
                || base.length != derivative.length) {
            return Integer.MAX_VALUE;
        }

        int sum = 0;
        for (int index=0; index < base.length; ++index) {
            int diff = degreesOfSeparation(base[index], derivative[index]);
            if (diff > 0) {
                return Integer.MAX_VALUE;
            }
            sum += diff;
        }

         return sum;
    }

    /**
     * Returns the degree of relationship of the derivative to the reference where:
     * 0 - the classes are equal
     * negative - the levels of ancestry (narrowing) from base to derivative
     * positive - the levels of ancestry (widening) from base to derivative
     * Integer.MAX_VALUE - no derivation relationship
     *
     * NOTE: the results are consistent with comparator to result in a collection
     * sorted from most to least specific assignable class
     *
     * e.g.
     * class X{}
     * class Y extends X{}
     * class Z extends Y{}
     * class W{}
     *
     * degreesOfSeparation(X.class, X.class) = 0
     * degreesOfSeparation(X.class, Y.class) = 1
     * degreesOfSeparation(X.class, Z.class) = 2
     * degreesOfSeparation(X.class, W.class) = Integer.MAX_VALUE
     *
     * degreesOfSeparation(Y.class, X.class) = -1
     * degreesOfSeparation(Z.class, X.class) = -2
     *
     * @param base
     * @param derivative
     * @return
     */
    public static final int degreesOfSeparation(final Class<?> base, final Class<?> derivative) {
        if (base.equals(derivative)) {
            return 0;

        } else if (base.isAssignableFrom(derivative)) {
            // if base is a superclass of derivative
            Class<?> superclass = derivative.getSuperclass();
            if (superclass != null && base.isAssignableFrom(superclass)) {
                return degreesOfSeparation(base, superclass) - 1;
            } else {
                // looking for a path through interfaces is more complex as multiple
                // paths may be available
                int shortestPath = Integer.MIN_VALUE;
                for (Class intf : derivative.getInterfaces()) {
                    if (base.isAssignableFrom(intf)) {
                        // max() is correct, it is the greatest negative number we're looking for
                        shortestPath = Math.max(shortestPath, degreesOfSeparation(base, intf) - 1);
                    }
                }
                return shortestPath;
            }

        } else if (derivative.isAssignableFrom(base)) {
            return degreesOfSeparation(derivative, base) * -1;
        }

        else {
            return Integer.MAX_VALUE;
        }
    }
}
