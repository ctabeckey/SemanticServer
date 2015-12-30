package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.fail;

/**
 *
 */
public class CommandClassSemanticsTest 
{
    private ApplicationSemantics applicationSemantics;

    @BeforeTest
    public void beforeTest() throws CoreRouterSemanticsException {
        this.applicationSemantics = new ApplicationSemantics("com.paypal.credit.testValidWorkflow.model");
    }

    @DataProvider
    public Object[][] validCommandClassData() {
        return new Object[][] {
                new Object[]{"GetAuthorizationCommand", Action.GET, "Authorization", null, null, null},
                new Object[]{"GetAuthorizationByAuthorizationIdCommand", Action.GET, "Authorization", null, Preposition.BY, "AuthorizationId"}
        };
    }

    @Test(dataProvider = "validCommandClassData")
	public void testValidCommandClassName(
			final String stimulus,
            final Action expectedAction,
            final String expectedClass,
            final CollectionType expectedCollectionType,
            final Preposition expectedPreposition,
            final String expectedObject)
	throws CoreRouterSemanticsException
	{
        CommandClassSemantics commandSemantics = this.applicationSemantics.createCommandClassSemantic(stimulus);
		assertEquals(commandSemantics.getAction(), expectedAction);
        assertEquals(commandSemantics.getSubject(), expectedClass);
        assertEquals(commandSemantics.getCollectionType(), expectedCollectionType);
        assertEquals(commandSemantics.getPreposition(), expectedPreposition);
        assertEquals(commandSemantics.getObject(), expectedObject);
	}

    @DataProvider
    public Object[][] invalidCommandClassData() {
        return new Object[][] {
                new Object[]{"GetAuthorizationBy", Action.GET, "Authorization", null, Preposition.BY, null}
        };
    }

    @Test(dataProvider = "invalidCommandClassData", expectedExceptions = {CoreRouterSemanticsException.class})
    public void testInvalidCommandClassName(
            final String stimulus,
            final Action expectedAction,
            final String expectedClass,
            final CollectionType expectedCollectionType,
            final Preposition expectedPreposition,
            final String expectedObject)
            throws CoreRouterSemanticsException
    {
        CommandClassSemantics commandSemantics = this.applicationSemantics.createCommandClassSemantic(stimulus);
    }
}
