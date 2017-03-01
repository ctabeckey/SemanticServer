package com.paypal.credit.core.applicationbridge;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.core.Application;
import com.paypal.credit.core.ApplicationImpl;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.utility.URLFactory;
import com.paypal.credit.test.FacadeTransactionContext;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;
import com.paypal.credit.test.processorbridge.SubjectProcessorBridge;
import com.paypal.credit.xactionctx.TransactionContextFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * Created by cbeckey on 11/12/15.
 */
public class ProcessorBridgeDefaultFactoryTest {
    private Application application;
    private ApplicationBridgeProxyImplFactory processorBridgeFactory;

    @BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    @BeforeTest
    public void b4Test() throws CoreRouterSemanticsException, FileNotFoundException, JAXBException, ContextInitializationException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        application = ApplicationImpl.create(true);

        this.processorBridgeFactory = new ApplicationBridgeProxyImplFactory(application);
    }

    @Test
    public void test() throws ProcessorBridgeInstantiationException {
        FacadeTransactionContext ctx = TransactionContextFactory.get(FacadeTransactionContext.class);
        ctx.setRoutingToken(new ProductTypeRoutingToken("USAINS"));
        ctx.setTransactionStartTime(System.currentTimeMillis());

        SubjectProcessorBridge bridge = this.processorBridgeFactory.create(SubjectProcessorBridge.class);
        Assert.assertNotNull(bridge);

        Authorization authorization = new Authorization();
        AuthorizationId id = bridge.postAuthorization(authorization);

        Assert.assertNotNull(id);
    }
}
