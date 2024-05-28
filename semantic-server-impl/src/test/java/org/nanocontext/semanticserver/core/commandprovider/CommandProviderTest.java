package org.nanocontext.semanticserver.core.commandprovider;

import org.nanocontext.semanticserver.semanticserver.commandprovider.RootCommandProvider;
import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;
import org.nanocontext.semanticserverapi.core.commandprovider.CommandInstantiationToken;
import org.nanocontext.semanticserverapi.core.commandprovider.exceptions.CommandProviderException;
import org.nanocontext.semanticserverapi.core.semantics.ApplicationSemantics;
import org.nanocontext.semanticserver.semanticserver.semantics.ApplicationSemanticsImpl;
import org.nanocontext.semanticserverapi.core.semantics.CommandClassSemantics;
import org.nanocontext.semanticserverapi.core.semantics.ProcessorBridgeMethodSemantics;
import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;
import org.nanocontext.semanticserver.semanticserver.applicationbridge.ProductTypeRoutingToken;
import org.nanocontext.semanticserver.test.model.Authorization;
import org.nanocontext.semanticserver.test.model.AuthorizationId;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/11/15.
 */
public class CommandProviderTest {
    private ApplicationSemantics applicationSemantics;
    private RootCommandProvider rootCommandProvider;

    @BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    @BeforeTest
    public void b4Test() throws CoreRouterSemanticsException {
        rootCommandProvider = RootCommandProvider.getOrCreate();
        applicationSemantics = ApplicationSemanticsImpl.create("com.paypal.semanticserver.test.model");
    }

    @Test
    public void testFindCommand() throws CoreRouterSemanticsException, CommandProviderException {
        ProcessorBridgeMethodSemantics rms = applicationSemantics.createProcessorBridgeMethodSemantics("postAuthorization");
        CommandClassSemantics ccs = applicationSemantics.createCommandClassSemantic(rms);

        RoutingToken rt = new ProductTypeRoutingToken("USACON");

        CommandInstantiationToken commandRank =
                rootCommandProvider.findCommand(rt, ccs, new Class[]{Authorization.class}, AuthorizationId.class);
        Assert.assertNotNull(commandRank);
        Assert.assertNotNull(commandRank.getCommandProvider());

    }
}
