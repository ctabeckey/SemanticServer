package org.nanocontext.semanticserver.semanticserver.applicationbridge;

import org.nanocontext.semanticserver.semanticserver.applicationbridge.exceptions.UnmappableCommandException;
import org.nanocontext.semanticserver.semanticserver.commandprocessor.exceptions.UnknownCommandException;
import org.nanocontext.semanticserverapi.core.Application;
import org.nanocontext.semanticserverapi.core.commandprocessor.AsynchronousExecutionCallback;
import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;
import org.nanocontext.semanticserverapi.core.semantics.CommandClassSemantics;
import org.nanocontext.semanticserverapi.core.semantics.ProcessorBridgeMethodSemantics;
import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Utility class, usable ONLY by members of this package.
 */
class Utility {

    /**
     *
     * @param method
     * @return
     * @throws UnmappableCommandException
     */
    static CommandClassSemantics getCommandClassSemantics(final Application application, final Method method)
            throws UnmappableCommandException {
        CommandClassSemantics commandClassSemantics;
        CommandMapping commandMapping = method.getAnnotation(CommandMapping.class);

        // if the method has a CommandMapping annotation, use it to create the command class semantics
        if (commandMapping != null) {
            try {
                commandClassSemantics = application.getApplicationSemantics().createCommandClassSemantic(
                        commandMapping.value().getName()
                );
            } catch (CoreRouterSemanticsException crsX) {
                throw new UnmappableCommandException(commandMapping);
            }

            // otherwise, reflect on the method to determine the command class semantics
        } else {
            // parse the Method into processor bridge semantics
            // if this is not successful then the processor bridge method does not meet semantics and must be
            // fixed in code.
            ProcessorBridgeMethodSemantics methodSemantics = null;
            try {
                methodSemantics =
                        application.getApplicationSemantics().createProcessorBridgeMethodSemantics(method);
                commandClassSemantics =
                        application.getApplicationSemantics().createCommandClassSemantic(methodSemantics);
            } catch (CoreRouterSemanticsException crsX) {
                throw new UnmappableCommandException(method);
            }
        }

        return commandClassSemantics;
    }

    /**
     * Create a Command instance from the Method, arguments, RoutingToken,
     * and other stuff.
     * The Command returned will correspond to the given semantics, be capable
     * of taking the parameter types and will return the given type.
     *
     * NOTE: if the Method is annotated with AsynchronousExecution and the
     * first parameter is assignable to AsynchronousExecutionCallback then
     * the first parameter is NOT used to match to the Command parameters.
     *
     * @param method
     * @param args
     * @param routingToken
     * @param commandClassSemantics
     * @param commandResultType
     * @return
     * @throws UnknownCommandException
     */
    public static <R> Callable<R> createCommand(
            final Application application,
            final Method method,
            final Object[] args,
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<R> commandResultType)
            throws UnknownCommandException {
        // Note: parameterAnnotations and args MUST (and will) be the same length
        // There are no standard parameter annotations defined by the framework,
        // but the parameter annotations may include annotations defined and/or
        // used by the command providers.
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        // if the method is marked for asynchronous execution
        // and the first arg is a callback class
        // then remove the callback class from the argument list for the Command
        Object[] commandArgs = args;
        AsynchronousExecution asynchExecution = method.getAnnotation(AsynchronousExecution.class);
        if (asynchExecution != null && args.length >= 1 && AsynchronousExecutionCallback.class.isInstance(args[0])) {
            commandArgs = new Object[args.length-1];
            System.arraycopy(args, 1, commandArgs, 0, args.length-1);
        }

        // find the mapped command
        try {
            Callable<R> command = application.getRootCommandProvider().createCommand(
                    routingToken,
                    commandClassSemantics,
                    commandArgs,
                    commandResultType
            );

            if (command == null) {
                throw new UnmappableCommandException(method, commandClassSemantics);
            }

            return command;
        } catch (Throwable t) {
            throw new UnknownCommandException(commandClassSemantics, t);
        }
    }

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

    // ==========================================================================================
    // Utility methods for special handling of an AsynchronousExecutionCallback reference.
    // A AsynchronousExecutionCallback reference as the first parameter of a processor
    // bridge method is not used when finding a command implementation.
    // ==========================================================================================

    /**
     *
     * @param args
     * @return
     */
    public static boolean includesAsynchronousExecutionCallback(final Object[] args) {
        return args != null
                && args.length >= 1
                && AsynchronousExecutionCallback.class.isInstance(args[0]);
    }
    public static AsynchronousExecutionCallback<?> extractAsynchronousExecutionCallback(final Object[] args) {
        if (includesAsynchronousExecutionCallback(args)) {
            return (AsynchronousExecutionCallback)args[0];
        }
        return null;
    }
    public static Object[] effectiveCommandArgs(final Object[] args) {
        if (includesAsynchronousExecutionCallback(args)) {
            Object[] commandArgs = new Object[args.length-1];
            System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);
            return commandArgs;
        } else {
            return args;
        }
    }

    /**
     *
     * @param parameterTypes
     * @return
     */
    public static boolean includesAsynchronousExecutionCallback(final Class<?>[] parameterTypes) {
        return parameterTypes != null
                && parameterTypes.length >= 1
                && AsynchronousExecutionCallback.class.isAssignableFrom(parameterTypes[0]);
    }

    public static Class<?>[] effectiveCommandParameterList(final Class<?>[] parameterTypes) {
        if (includesAsynchronousExecutionCallback(parameterTypes)) {
            Class<?>[] commandParameterTypes = new Class<?>[parameterTypes.length-1];
            System.arraycopy(parameterTypes, 1, commandParameterTypes, 0, commandParameterTypes.length);
            return commandParameterTypes;
        } else {
            return parameterTypes;
        }
    }
}
