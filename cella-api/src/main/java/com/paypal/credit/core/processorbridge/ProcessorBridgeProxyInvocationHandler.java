package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.ApplicationTransactionContext;
import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprocessor.exceptions.UnknownCommandException;
import com.paypal.credit.core.processorbridge.exceptions.InvalidTransactionContextException;
import com.paypal.credit.core.processorbridge.exceptions.UnmappableCommandException;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

        CommandClassSemantics commandClassSemantics = getCommandClassSemantics(method);

        Command command = createCommand(method, args, routingToken, commandClassSemantics, method.getReturnType());

        return executeCommand(commandClassSemantics, command);
    }
}
