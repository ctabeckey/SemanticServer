package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**

 /* *
 *
 */
public class CommandFactoryMethodSemanticsTest 
{
    private ApplicationSemantics applicationSemantics;

    @BeforeTest
    public void beforeTest() throws CoreRouterSemanticsException {
        this.applicationSemantics = new ApplicationSemantics("com.paypal.credit.test.model");
    }

    /**
     * Test stimulus and expected results
     * Each row should include:
     *   stimulus (commandprovider name),
     *   action,
     *   subject,
     *   [collection type],
     *   [proposition, object of preposition]
     * @return
     */
    @DataProvider
    public Object[][] validElementsData() {
        return new Object[][]{
                new Object[]{"createGetAccountCommand", Action.GET, "Account", null, null, null},
                new Object[]{"createGetAccountListCommand", Action.GET, "Account", CollectionType.LIST, null, null},
                new Object[]{"createPostAuthorizationCommand", Action.POST, "Authorization", null, null, null},
        };
    };

    @Test(dataProvider = "validElementsData")
	public void testKnownValidElements(
            String methodName,
            Action action,
            String subject,
            CollectionType subjectCollectionType,
            Preposition preposition,
            String objectOfPreposition)
	throws CoreRouterSemanticsException
	{
        CommandFactoryMethodSemantics command =
            this.applicationSemantics.createFactoryMethodSemantic(methodName);

        assertEquals(command.getAction(), action);
        assertEquals(command.getSubject(), subject);
        assertEquals(command.getCollectionType(), subjectCollectionType);
        assertEquals(command.getPreposition(), preposition);
        assertEquals(command.getObject(), objectOfPreposition);
    }

}
