package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class CommandClassSemanticsTest 
{
    private ApplicationSemantics applicationSemantics;

    //@BeforeTest
    public void beforeTest() throws CoreRouterSemanticsException {
        this.applicationSemantics = ApplicationSemanticsImpl.create("com.paypal.credit.testValidWorkflow.model");
    }

    //@DataProvider
    public Object[][] validCommandClassData() {
        VocabularyWord get = this.applicationSemantics.getActionVocabulary().find("Get");
        VocabularyWord by = this.applicationSemantics.getPrepositionVocabulary().find("By");

        return new Object[][] {
                new Object[]{"GetAuthorizationCommand", get, "Authorization", null, null, null},
                new Object[]{"GetAuthorizationByAuthorizationIdCommand", get, "Authorization", null, by, "AuthorizationId"}
        };
    }

    //@Test(dataProvider = "validCommandClassData")
	public void testValidCommandClassName(
			final String stimulus,
            final VocabularyWord expectedAction,
            final String expectedSubject,
            final CollectionType expectedCollectionType,
            final VocabularyWord expectedPreposition,
            final String expectedObject)
	throws CoreRouterSemanticsException
	{
        CommandClassSemantics commandSemantics = this.applicationSemantics.createCommandClassSemantic(stimulus);
		assertEquals(commandSemantics.getAction(), expectedAction);
        assertEquals(commandSemantics.getSubject(), expectedSubject);
        assertEquals(commandSemantics.getCollectionType(), expectedCollectionType);
        assertEquals(commandSemantics.getPreposition(), expectedPreposition);
        assertEquals(commandSemantics.getObject(), expectedObject);
	}

    //@DataProvider
    public Object[][] invalidCommandClassData() {
        VocabularyWord get = this.applicationSemantics.getActionVocabulary().find("Get");
        VocabularyWord by = this.applicationSemantics.getPrepositionVocabulary().find("By");

        return new Object[][] {
                new Object[]{"GetAuthorizationBy", get, "Authorization", null, by, null}
        };
    }

    //@Test(dataProvider = "invalidCommandClassData", expectedExceptions = {CoreRouterSemanticsException.class})
    public void testInvalidCommandClassName(
            final String stimulus,
            final VocabularyWord expectedAction,
            final String expectedClass,
            final CollectionType expectedCollectionType,
            final VocabularyWord expectedPreposition,
            final String expectedObject)
            throws CoreRouterSemanticsException
    {
        CommandClassSemantics commandSemantics = this.applicationSemantics.createCommandClassSemantic(stimulus);
    }
}
