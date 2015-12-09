package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.ApplicationTransactionContext;
import com.paypal.credit.core.commandprocessor.AsynchronousExecutionCallback;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.processorbridge.exceptions.InvalidTransactionContextException;
import com.paypal.credit.core.processorbridge.exceptions.UnmappableCommandException;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 *
 */
public class ProcessorBridgeProxyInvocationHandler
extends AbstractProcessorBridgeImpl
implements InvocationHandler {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProcessorBridgeProxyInvocationHandler.class);

    /**
     *
     * @param application
     */
    public ProcessorBridgeProxyInvocationHandler(Application application) {
        super(application);
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws Exception {
        ApplicationTransactionContext ctx = TransactionContextFactory.get(ApplicationTransactionContext.class);
        RoutingToken routingToken = ctx.getRoutingToken();
        if (routingToken == null) {
            throw new InvalidTransactionContextException("getRoutingToken");
        }

        return findAndInvokeCommand(method, args, routingToken, method.getReturnType());
    }

    /**
     *
     * @param method
     * @param args
     * @param routingToken
     * @return
     * @throws UnmappableCommandException
     * @throws com.paypal.credit.core.commandprocessor.exceptions.UnknownCommandException
     * @throws com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException
     */
    private <R> R findAndInvokeCommand(
            final Method method,
            final Object[] args,
            final RoutingToken routingToken,
            final Class<R> resultType)
    throws UnmappableCommandException, com.paypal.credit.core.commandprocessor.exceptions.UnknownCommandException, com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException {
        CommandClassSemantics commandClassSemantics = getCommandClassSemantics(method);
        Callable<R> command =
                createCommand(method, args, routingToken, commandClassSemantics, resultType);

        if (method.getAnnotation(AsynchronousExecution.class) != null) {
            AsynchronousExecutionCallback<R> callback = (AsynchronousExecutionCallback<R>) extractAsynchronousExecutionCallback(args);
            submitCommand(command, callback);
            return null;
        } else {
            return executeCommand(command);
        }
    }


    /**
     *
     * @param args
     * @return
     */
    private AsynchronousExecutionCallback<?> extractAsynchronousExecutionCallback(final Object[] args) {
        if (args != null && args.length >= 1 && AsynchronousExecutionCallback.class.isInstance(args[0])) {
            return (AsynchronousExecutionCallback)args[0];
        }
        return null;
    }
}
