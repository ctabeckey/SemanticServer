package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.semantics.AbstractBaseSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;

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
}
