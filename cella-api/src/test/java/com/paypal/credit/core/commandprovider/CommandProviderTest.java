package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.semantics.ApplicationSemantics;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.core.utility.URLFactory;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;
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
        applicationSemantics = new ApplicationSemantics("com.paypal.credit.test.model");
    }

    @Test
    public void testFindCommand() throws CoreRouterSemanticsException {
        ProcessorBridgeMethodSemantics rms = applicationSemantics.createProcessorBridgeMethodSemantics("postAuthorization");
        CommandClassSemantics ccs = applicationSemantics.createCommandClassSemantic(rms);

        RoutingToken rt = new ProductTypeRoutingToken("USAINS");

        CommandInstantiationToken commandRank =
                rootCommandProvider.findMostApplicableCommand(rt, ccs, new Class[]{Authorization.class}, AuthorizationId.class);
        Assert.assertNotNull(commandRank);
        Assert.assertNotNull(commandRank.getCommandProvider());
    }
}
