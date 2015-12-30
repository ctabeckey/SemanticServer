package com.paypal.credit.core.utility;

/**
 * Utility for basic null parameter checking to standardize exception message format.
 */
public final class ParameterCheckUtility {

    /**
     * Private constructor to prevent instantiation
     */
    private ParameterCheckUtility() {
    }

    /**
     * Checks if an object is null. If so, throws an IllegalArgumentException.
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     */
    public static void checkParameterNotNull(final Object parameter, final String parameterName) {
        if (parameter == null) {
            throw new IllegalArgumentException("'" + parameterName + "' is null and must not be.");
        }
    }

    /**
     * Checks if a String is null or of length zero. If so, throws an IllegalArgumentException.
     * @param parameter the parameter to check
     * @param parameterName the name of the parameter
     */
    public static void checkParameterNotNullOrEmpty(final String parameter, final String parameterName) {
        checkParameterNotNull(parameter, parameterName);
        if (parameter.isEmpty()) {
            throw new IllegalArgumentException("'" + parameterName + "' is empty (zero-length) and must not be.");
        }
    }

    /**
     *
     * @param parameter
     * @param parameterName
     */
    public static void checkParameterStrictlyPositive(final int parameter, final String parameterName) {
        if (parameter <= 0) {
            throw new IllegalArgumentException("'" + parameterName + "' must be strictly positive");
        }
    }

    /**
     *
     * @param parameter
     * @param parameterName
     */
    public static void checkParameterNotNegative(final int parameter, final String parameterName) {
        if (parameter < 0) {
            throw new IllegalArgumentException("'" + parameterName + "' must not be negative");
        }
    }
}