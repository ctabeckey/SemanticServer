package com.paypal.credit.core.commandprovider.exceptions;

/**
 * Created by cbeckey on 11/17/15.
 */
public class InvalidTokenException extends CommandProviderException {
    private static String createMessage(Class<?> tokenClass, Class<?> expectedTokenClass) {
        return String.format("Invalid token type, actual type was %s, expected type is %s",
                tokenClass == null ? "<null>" : tokenClass.getName(),
                expectedTokenClass == null ? "<null>" : expectedTokenClass.getName());
    }

    public InvalidTokenException(Class<?> tokenClass, Class<?> expectedTokenClass) {
        super(createMessage(tokenClass, expectedTokenClass));
    }
}
