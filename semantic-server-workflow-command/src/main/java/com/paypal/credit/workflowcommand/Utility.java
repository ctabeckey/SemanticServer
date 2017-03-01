package com.paypal.credit.workflowcommand;

/**
 * Utility class, usable ONLY by members of this package.
 */
class Utility {

    // ==========================================================================================
    //
    // ==========================================================================================
    public static Class<?>[] getParameterTypes(Object[] args) {
        Class<?>[] parameterTypes = new Class<?>[args == null ? 0 : args.length];

        for (int index=0; index < args.length; ++index) {
            parameterTypes[index] = args[index].getClass();
        }

        return parameterTypes;
    }
}
