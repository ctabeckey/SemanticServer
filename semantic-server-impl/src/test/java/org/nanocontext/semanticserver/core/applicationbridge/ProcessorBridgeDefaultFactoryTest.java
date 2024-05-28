package org.nanocontext.semanticserver.core.applicationbridge;

import org.nanocontext.semanticserver.semanticserver.applicationbridge.ApplicationBridgeProxyImplFactory;
import org.nanocontext.semanticserver.semanticserver.applicationbridge.ProductTypeRoutingToken;
import org.nanocontext.semanticserverapi.core.Application;
import org.nanocontext.semanticserver.ApplicationImpl;
import org.nanocontext.semanticserver.semanticserver.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;
import org.nanocontext.semanticserver.test.FacadeTransactionContext;
import org.nanocontext.semanticserver.test.model.Authorization;
import org.nanocontext.semanticserver.test.model.AuthorizationId;
import org.nanocontext.semanticserver.test.processorbridge.SubjectProcessorBridge;
import org.testng.Assert;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

/**
 * Created by cbeckey on 11/12/15.
 */
public class ProcessorBridgeDefaultFactoryTest {
    private Application application;
    private ApplicationBridgeProxyImplFactory processorBridgeFactory;

    //@BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    //@BeforeTest
    public void b4Test() throws CoreRouterSemanticsException, FileNotFoundException, JAXBException, ContextInitializationException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        application = ApplicationImpl.create(true);

        this.processorBridgeFactory = new ApplicationBridgeProxyImplFactory(application);
    }

    //@Test
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
