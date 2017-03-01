package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 *
 */
public class ProcessorBridgeMethodSemanticsTest
{
    private ApplicationSemantics applicationSemantics;

    @BeforeTest
    public void beforeTest() throws CoreRouterSemanticsException {
        this.applicationSemantics = ApplicationSemanticsImpl.create("com.paypal.credit.testValidWorkflow.model");
    }

    @DataProvider
    public Object[][] validRouterMethodData() {
        VocabularyWord get = this.applicationSemantics.getActionVocabulary().find("Get");
        VocabularyWord post = this.applicationSemantics.getActionVocabulary().find("Post");
        VocabularyWord by = this.applicationSemantics.getPrepositionVocabulary().find("By");

        return new Object[][] {
                new Object[]{"postAuthorization", post, "Authorization", null, null, null, "PostAuthorizationCommand", "createPostAuthorizationCommand"},
                new Object[]{"getAuthorizationByAuthorizationId", get, "Authorization", null, by, "AuthorizationId", "GetAuthorizationByAuthorizationIdCommand", "createGetAuthorizationByAuthorizationIdCommand"}
        };
    }

    @Test(dataProvider = "validRouterMethodData")
    public void testValidRouterMethodName(
            final String stimulus,
            final VocabularyWord expectedAction,
            final String expectedClass,
            final CollectionType expectedCollectionType,
            final VocabularyWord expectedPreposition,
            final String expectedObject,
            final String expectedCommandName,
            final String expectedCommandFactoryMethodName)
            throws CoreRouterSemanticsException
    {
        ProcessorBridgeMethodSemantics commandSemantics = this.applicationSemantics.createProcessorBridgeMethodSemantics(stimulus);
        assertEquals(commandSemantics.getAction(), expectedAction);
        assertEquals(commandSemantics.getSubject(), expectedClass);
        assertEquals(commandSemantics.getCollectionType(), expectedCollectionType);
        assertEquals(commandSemantics.getPreposition(), expectedPreposition);
        assertEquals(commandSemantics.getObject(), expectedObject);

        CommandClassSemantics commandClassSemantics = this.applicationSemantics.createCommandClassSemantic(commandSemantics);
        assertEquals(commandClassSemantics.toString(), expectedCommandName);

        CommandFactoryMethodSemantics commandFactoryMethodSemantics = this.applicationSemantics.createFactoryMethodSemantic(commandSemantics);
        assertEquals(commandFactoryMethodSemantics.toString(), expectedCommandFactoryMethodName);
    }

    @DataProvider
    public Object[][] invalidRouterMethodData() {
        return new Object[][] {
                new Object[]{"getAuthorizationBy"}
        };
    }

    @Test(dataProvider = "invalidRouterMethodData", expectedExceptions = {CoreRouterSemanticsException.class})
    public void testInvalidCommandClassName(
            final String stimulus)
            throws CoreRouterSemanticsException
    {
        ProcessorBridgeMethodSemantics commandSemantics = this.applicationSemantics.createProcessorBridgeMethodSemantics(stimulus);
    }


}
