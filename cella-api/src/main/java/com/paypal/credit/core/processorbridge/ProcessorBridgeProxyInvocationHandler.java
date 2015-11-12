package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeDefinesUnmappableMethodException;
import com.paypal.credit.core.commandprovider.ConstructorRankingList;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 */
public class ProcessorBridgeProxyInvocationHandler
implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorBridgeProxyInvocationHandler.class);
    private final Application application;

    public ProcessorBridgeProxyInvocationHandler(Application application) {
        ParameterCheckUtility.checkParameterNotNull(application, "application");

        LOGGER.info("ProcessorBridgeProxyInvocationHandler <ctor>");
        this.application = application;
    }

    /**
     * {@InheritDoc}
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws Exception {
        RoutingToken routingToken = new RoutingToken(){
            @Override
            public int compareTo(final RoutingToken o) {
                return 0;
            }
        };

        // parse the Method into processor bridge semantics
        // if this is not successful then the processor bridge method does not meet semantics and must be
        // fixed in code.
        ProcessorBridgeMethodSemantics methodSemantics = null;
        try {
            methodSemantics =
                    application.getApplicationSemantics().createProcessorBridgeMethodSemantics(method);
        } catch (CoreRouterSemanticsException crsX) {
            throw new UnmappableCommandException(method);
        }

        // find and execute the mapped command
        try {
            CommandClassSemantics ccs = application.getApplicationSemantics().createCommandClassSemantic(methodSemantics);
            Command command = application.getCommandProvider().createCommand(routingToken, ccs, args, method.getReturnType());

            if (command == null) {
                throw new UnmappableCommandException(method, ccs);
            }

            Object result = application.getCommandProcessor().doSynchronously(command);

            return result;
        } catch (CoreRouterSemanticsException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
