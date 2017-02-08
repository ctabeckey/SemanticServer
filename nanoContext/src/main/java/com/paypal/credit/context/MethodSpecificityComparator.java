package com.paypal.credit.context;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by cbeckey on 1/20/17.
 */
public class MethodSpecificityComparator implements Comparator<Method> {
    @Override
    public int compare(final Method method1, final Method method2) {
        if (method1.getParameterTypes().length < method2.getParameterTypes().length) {
            return -1;
        } else if (method1.getParameterTypes().length < method2.getParameterTypes().length) {
            return 1;
        }
        for (int index = 0; index < method1.getParameterTypes().length; ++index) {
            if (method1.getParameterTypes()[index].equals(method2.getParameterTypes()[index])) {
                continue;
            } else if (method1.getParameterTypes()[index].isAssignableFrom(method2.getParameterTypes()[index])) {
                return 2;
            } else if (method2.getParameterTypes()[index].isAssignableFrom(method1.getParameterTypes()[index])) {
                return -2;

            } else if (String.class.equals(method1.getParameterTypes()[index])
                    && !String.class.equals(method2.getParameterTypes()[index])) {
                // one of two odd cases where a constructor may take a type that can be constructed
                // using a valueOf(String) method. Constructors taking a String should be sorted later
                // than an otherwise equivalent constructor (taking an Integer for instance)
                return 3;
            } else if (String.class.equals(method2.getParameterTypes()[index])
                    && !String.class.equals(method1.getParameterTypes()[index])) {
                return -3;
            }
        }
        return 0;
    }
}
