package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/10/15.
 */
public class LogicalNameParserTest {

    @DataProvider
    public Object[][] validLogicalNames() {
        return new Object[][] {
                new Object[]{"GetAuthorization", Action.GET, "Authorization", null, null, null},
                new Object[]{"GetAuthorizationList", Action.GET, "Authorization", CollectionType.LIST, null, null},
                new Object[]{"GetAuthorizationByAuthorizationId", Action.GET, "Authorization", null, Preposition.BY, "AuthorizationId"},
                new Object[]{"GetAuthorizationListByAuthorizationId", Action.GET, "Authorization", CollectionType.LIST, Preposition.BY, "AuthorizationId"},
        };
    }

    @Test (dataProvider = "validLogicalNames")
    public void testValidLogicalNames(
            final String stimulus,
            final Action expectedAction,
            final String expectedSubject,
            final CollectionType expectedCollectionType,
            final Preposition expectedPreposition,
            final String expectedObject) throws CoreRouterSemanticsException {

        LogicalNameParser parser = new LogicalNameParser(stimulus);
        Assert.assertEquals(parser.getAction(), expectedAction);
        Assert.assertEquals(parser.getSubject(), expectedSubject);
        Assert.assertEquals(parser.getCollectionType(), expectedCollectionType);
        Assert.assertEquals(parser.getPreposition(), expectedPreposition);
        Assert.assertEquals(parser.getObject(), expectedObject);
    }

    @DataProvider
    public Object[][] invalidLogicalNames() {
        return new Object[][] {
                new Object[]{""},
                new Object[]{"Authorization"},
                new Object[]{"Get"},
                new Object[]{"chooseAuthorizationByAuthorizationId"},
                new Object[]{"GetAuthorizationListBy"},
        };
    }

    @Test (dataProvider = "invalidLogicalNames", expectedExceptions = {CoreRouterSemanticsException.class})
    public void testInvalidLogicalNames(
            final String stimulus) throws CoreRouterSemanticsException {

        LogicalNameParser parser = new LogicalNameParser(stimulus);
    }
}
