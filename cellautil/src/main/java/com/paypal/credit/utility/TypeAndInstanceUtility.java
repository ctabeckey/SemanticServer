package com.paypal.credit.utility;

/**
 * Created by cbeckey on 11/13/15.
 */
public class TypeAndInstanceUtility {
    /**
     *
     * @param parameters
     * @return
     */
    public static Class<?>[] getTypes(final Object[] parameters) {
        final Class<?>[] parameterTypes = new Class<?>[parameters.length];

        for(int index = 0; index < parameters.length; ++index) {
            parameterTypes[index] = parameters[index].getClass();
        }
        return parameterTypes;
    }
}
