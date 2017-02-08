package com.paypal.credit.context;

import java.lang.reflect.Constructor;
import java.util.Comparator;

/**
 * Created by cbeckey on 1/20/17.
 */
public class ConstructorSpecificityComparator<T> implements Comparator<Constructor<T>> {
    @Override
    public int compare(final Constructor<T> ctor1, final Constructor<T> ctor2) {
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
}
