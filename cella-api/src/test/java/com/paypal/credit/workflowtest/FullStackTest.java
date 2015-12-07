package com.paypal.credit.workflowtest;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.processorbridge.ProcessorBridgeProxyImplFactory;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.utility.URLFactory;
import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;
import com.paypal.credit.workflowtest.processorbridge.WorkflowTestProcessorBridge;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 12/7/15.
 */
public class FullStackTest {
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

    /**
     * This is a REST or message endpoint simulation.
     *
     * @throws ProcessorBridgeInstantiationException
     */
    @Test
    public void test() {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);

        ctx.setRoutingToken(new ProductTypeRoutingToken("usains"));
        AuthorizationId id = new AuthorizationId();
        Authorization authorization = bridge.getAuthorization(id);

    }
}
