package com.paypal.credit.workflowtest;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.AsynchronousExecutionCallback;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.processorbridge.ProcessorBridgeProxyImplFactory;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.utility.URLFactory;
import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;
import com.paypal.credit.workflowtest.processorbridge.WorkflowTestProcessorBridge;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 12/7/15.
 */
public class FullStackTest {
    private Logger LOGGER = LoggerFactory.getLogger(FullStackTest.class);
    private Application application;
    private ProcessorBridgeProxyImplFactory processorBridgeFactory;
    private WorkflowTestProcessorBridge bridge;

    @BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    @BeforeTest
    public void b4Test() throws CoreRouterSemanticsException, ProcessorBridgeInstantiationException {
        application = Application.create("com.paypal.credit.workflowtest");
        processorBridgeFactory = new ProcessorBridgeProxyImplFactory(application);
        bridge = processorBridgeFactory.create(WorkflowTestProcessorBridge.class);
    }

    @AfterTest
    public void afterTest() {
        application.shutdown();

        while(!application.isShutdown()) {
            try {Thread.sleep(1000L);}
            catch(InterruptedException ix){break;}
        }
    }

    /**
     * This is a REST or message endpoint simulation.
     *
     * @throws ProcessorBridgeInstantiationException
     */
    @Test
    public void testSynchronousCall() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId("655321");
        Authorization authorization = bridge.getAuthorization(id);

    }

    @Test
    public void testAsynchronousCall() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId("655321");
        bridge.deleteAuthorization(id);
    }

    @Test
    public void testAsynchronousCallWithCallback() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId("655321");
        bridge.getAuthorization(
                new AsynchronousExecutionCallback<Authorization>() {
                    @Override
                    public void success(final Authorization result) {
                        System.out.println("Asynchronous command worked");
                    }

                    @Override
                    public void failure(final Throwable t) {
                        System.out.println("Asynchronous command failed");
                    }
                },
                id);
    }
}
