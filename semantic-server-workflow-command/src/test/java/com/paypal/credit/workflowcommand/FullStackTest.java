package com.paypal.credit.workflowcommand;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.core.Application;
import com.paypal.credit.core.ApplicationImpl;
import com.paypal.credit.core.commandprocessor.AsynchronousExecutionCallback;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.applicationbridge.ApplicationBridgeProxyImplFactory;
import com.paypal.credit.core.applicationbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.workflowcommand.model.Authorization;
import com.paypal.credit.workflowcommand.model.AuthorizationId;
import com.paypal.credit.workflowcommand.processorbridge.WorkflowTestProcessorBridge;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import com.paypal.utility.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * Created by cbeckey on 12/7/15.
 */
public class FullStackTest {
    private Logger LOGGER = LoggerFactory.getLogger(FullStackTest.class);
    private Application application;
    private ApplicationBridgeProxyImplFactory processorBridgeFactory;
    private WorkflowTestProcessorBridge bridge;

    @BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    @BeforeTest
    public void b4Test() throws CoreRouterSemanticsException, ProcessorBridgeInstantiationException, FileNotFoundException, JAXBException, ContextInitializationException {
        application = ApplicationImpl.create(true, "FullStackTestContext.xml");
        processorBridgeFactory = new ApplicationBridgeProxyImplFactory(application);
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
    public void testSynchronousGetCall() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId("655321");
        Authorization authorization = bridge.getAuthorization(id);
    }

    @Test
    public void testSynchronousDeleteCall() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId("655321");
        bridge.deleteAuthorization(id);
    }

    @Test
    public void testAsynchronousDeleteCall() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId("655321");
        bridge.deleteAuthorization(id);
    }

    @Test
    public void testAsynchronousGetCallWithCallback() {
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
