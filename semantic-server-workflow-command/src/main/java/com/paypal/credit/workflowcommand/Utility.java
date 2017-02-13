package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.AsynchronousExecutionCallback;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprocessor.exceptions.UnknownCommandException;
import com.paypal.credit.core.processorbridge.AsynchronousExecution;
import com.paypal.credit.core.processorbridge.CommandMapping;
import com.paypal.credit.core.processorbridge.exceptions.UnmappableCommandException;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

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
