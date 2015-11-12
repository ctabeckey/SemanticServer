package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;
import com.paypal.credit.test.processorbridge.SubjectProcessorBridge;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/12/15.
 */
public class ProcessorBridgeDefaultFactoryTest {
    private Application application;
    private ProcessorBridgeProxyImplFactory processorBridgeFactory;

    @BeforeTest
    public void b4Test() throws CoreRouterSemanticsException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        application = Application.create(classLoader, "com.paypal.credit.test");

        this.processorBridgeFactory = new ProcessorBridgeProxyImplFactory(application);
    }

    @Test
    public void test() throws ProcessorBridgeInstantiationException {
        SubjectProcessorBridge bridge = this.processorBridgeFactory.create(SubjectProcessorBridge.class);
        Assert.assertNotNull(bridge);

        Authorization authorization = new Authorization();
        AuthorizationId id = bridge.postAuthorization(authorization);

        Assert.assertNotNull(id);
    }
}
