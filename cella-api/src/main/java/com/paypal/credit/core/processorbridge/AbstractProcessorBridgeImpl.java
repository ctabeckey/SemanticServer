package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.CommandProcessor;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprocessor.exceptions.UnknownCommandException;
import com.paypal.credit.core.commandprovider.RootCommandProvider;
import com.paypal.credit.core.processorbridge.exceptions.UnmappableCommandException;
import com.paypal.credit.core.semantics.ApplicationSemantics;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * An abstract, base, implementation of a ProcessorBridge.
 * Contains convenience methods for ProcessorBridge implementations.
 */
public abstract class AbstractProcessorBridgeImpl {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractProcessorBridgeImpl.class);
    private final Application application;

    /**
     *
     * @param application
     */
    public AbstractProcessorBridgeImpl(Application application) {
        ParameterCheckUtility.checkParameterNotNull(application, "application");

        LOGGER.info("ProcessorBridgeProxyInvocationHandler <ctor>");
        this.application = application;
    }

    protected Application getApplication() {
        return application;
    }

    protected RootCommandProvider getRootCommandProvider() {
        return getApplication().getRootCommandProvider();
    }

    protected CommandProcessor getCommandProcessor() {
        return getApplication().getCommandProcessor();
    }

    protected ApplicationSemantics getApplicationSemantics() {
        return getApplication().getApplicationSemantics();
    }

    /**
     *
     * @param method
     * @return
     * @throws UnmappableCommandException
     */
    protected CommandClassSemantics getCommandClassSemantics(final Method method)
            throws UnmappableCommandException {
        CommandClassSemantics commandClassSemantics;
        CommandMapping commandMapping = method.getAnnotation(CommandMapping.class);

        // if the method has a CommandMapping annotation, use it to create the command class semantics
        if (commandMapping != null && commandMapping.commandClass() != null) {
            try {
                commandClassSemantics = getApplicationSemantics().createCommandClassSemantic(commandMapping.commandClass());
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
                        getApplicationSemantics().createProcessorBridgeMethodSemantics(method);
                commandClassSemantics =
                        getApplicationSemantics().createCommandClassSemantic(methodSemantics);
            } catch (CoreRouterSemanticsException crsX) {
                throw new UnmappableCommandException(method);
            }
        }

        return commandClassSemantics;
    }

    /**
     *
     * @param method
     * @param args
     * @param routingToken
     * @param commandClassSemantics
     * @param commandResultType
     * @return
     * @throws UnknownCommandException
     */
    protected Command createCommand(
            final Method method,
            final Object[] args,
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?> commandResultType)
            throws UnknownCommandException {
        // Note: parameterAnnotations and args MUST (and will) be the same length
        // There are no standard parameter annotations defined by the framework,
        // but the parameter annotations may include annotations defined and/or
        // used by the command providers.
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        // find the mapped command
        try {
            Command command = getRootCommandProvider().createCommand(
                    routingToken,
                    commandClassSemantics,
                    args,
                    parameterAnnotations,
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

    /**
     *
     * @param commandClassSemantics
     * @param command
     * @return
     * @throws CoreRouterSemanticsException
     * @throws UnknownCommandException
     */
    protected Object executeCommand(final CommandClassSemantics commandClassSemantics, final Command command) throws CoreRouterSemanticsException, UnknownCommandException {
        Object result;// execute the mapped command
        try {
            result = getCommandProcessor().doSynchronously(command);
        } catch (CoreRouterSemanticsException e) {
            e.printStackTrace();
            throw e;
        } catch (Throwable t) {
            throw new UnknownCommandException(commandClassSemantics, t);
        }
        return result;
    }

}
