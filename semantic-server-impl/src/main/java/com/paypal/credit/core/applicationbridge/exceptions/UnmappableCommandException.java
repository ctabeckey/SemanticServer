package com.paypal.credit.core.applicationbridge.exceptions;

import com.paypal.credit.core.applicationbridge.CommandMapping;
import com.paypal.credit.core.semantics.AbstractBaseSemantics;

import java.lang.reflect.Method;

/**
 * thrown when a processor bridge method cannot be mapped to an available command
 */
public class UnmappableCommandException extends Exception {
    private static String createMessage(final AbstractBaseSemantics semantics) {
        return String.format("Unable to find a command mapped to semantics '%s'", semantics.toString());
    }
    private static String createMessage(final Method bridgeMethod) {
        return String.format("Unable to find a command mapped to method '%s'", bridgeMethod.getName());
    }
    private static String createMessage(final Method method, final AbstractBaseSemantics abs) {
        return String.format("Method '%s' could not be mapped to a command (%s)", method.getName(), abs.toString());
    }
    private static String createMessage(final CommandMapping commandMapping) {
        return String.format("Unable to find a command explicitly named as '%s'", commandMapping.value().getName());
    }


    public UnmappableCommandException(final Method bridgeMethod) {
        super(createMessage(bridgeMethod));
    }

    public UnmappableCommandException(final Method bridgeMethod, Throwable cause) {
        super(createMessage(bridgeMethod), cause);
    }

    public UnmappableCommandException(final Method bridgeMethod, AbstractBaseSemantics abs) {
        super(createMessage(bridgeMethod, abs));
    }

    public UnmappableCommandException(final Method bridgeMethod, AbstractBaseSemantics abs, Throwable cause) {
        super(createMessage(bridgeMethod, abs), cause);
    }

    public UnmappableCommandException(final AbstractBaseSemantics semantics) {
        super(createMessage(semantics));
    }

    public UnmappableCommandException(final AbstractBaseSemantics semantics, final Throwable cause) {
        super(createMessage(semantics), cause);
    }

    public UnmappableCommandException(final CommandMapping commandMapping) {
        super(createMessage(commandMapping));
    }

    public UnmappableCommandException(final CommandMapping commandMapping, Throwable cause) {
        super(createMessage(commandMapping), cause);
    }
}
