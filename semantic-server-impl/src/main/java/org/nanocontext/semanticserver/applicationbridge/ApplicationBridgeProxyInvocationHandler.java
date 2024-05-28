package org.nanocontext.semanticserver.semanticserver.applicationbridge;

import org.nanocontext.semanticserver.semanticserver.ApplicationTransactionContext;
import org.nanocontext.semanticserver.semanticserver.applicationbridge.exceptions.InvalidTransactionContextException;
import org.nanocontext.semanticserver.semanticserver.applicationbridge.exceptions.UnmappableCommandException;
import org.nanocontext.semanticserver.semanticserver.commandprocessor.exceptions.UnknownCommandException;
import org.nanocontext.semanticserverapi.core.Application;
import org.nanocontext.semanticserverapi.core.commandprocessor.AsynchronousExecutionCallback;
import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;
import org.nanocontext.semanticserverapi.core.semantics.CommandClassSemantics;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * A processor bridge realization as a dynamic proxy invocation handler.
 */
public class ApplicationBridgeProxyInvocationHandler
extends AbstractApplicationBridge
implements InvocationHandler {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ApplicationBridgeProxyInvocationHandler.class);

    /**
     *
     * @param application
     */
    public ApplicationBridgeProxyInvocationHandler(Application application) {
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
     * @throws UnknownCommandException
     * @throws CoreRouterSemanticsException
     */
    private <R> R findAndInvokeCommand(
            final Method method,
            final Object[] args,
            final RoutingToken routingToken,
            final Class<R> resultType)
    throws UnmappableCommandException, UnknownCommandException, CoreRouterSemanticsException {
        CommandClassSemantics commandClassSemantics = Utility.getCommandClassSemantics(getApplication(), method);
        Callable<R> command =
                Utility.createCommand(getApplication(), method, args, routingToken, commandClassSemantics, resultType);

        if (method.getAnnotation(AsynchronousExecution.class) != null) {
            AsynchronousExecutionCallback<R> callback =
                    (AsynchronousExecutionCallback<R>) Utility.extractAsynchronousExecutionCallback(args);
            submitCommand(command, callback);
            return null;
        } else {
            return executeCommand(command);
        }
    }

}
