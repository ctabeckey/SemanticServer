package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/10/15.
 */
public class LogicalNameParserImplTest {
    private ApplicationSemantics applicationSemantics;

    @BeforeTest
    public void beforeTest() throws CoreRouterSemanticsException {
        this.applicationSemantics = ApplicationSemanticsImpl.create("com.paypal.credit.testValidWorkflow.model");
    }

    @DataProvider
    public Object[][] validLogicalNames() {
        VocabularyWord get = this.applicationSemantics.getActionVocabulary().find("Get");
        VocabularyWord by = this.applicationSemantics.getPrepositionVocabulary().find("By");

        return new Object[][] {
                new Object[]{"GetAuthorization", get, "Authorization", null, null, null},
                new Object[]{"GetAuthorizationList", get, "Authorization", CollectionType.LIST, null, null},
                new Object[]{"GetAuthorizationByAuthorizationId", get, "Authorization", null, by, "AuthorizationId"},
                new Object[]{"GetAuthorizationListByAuthorizationId", get, "Authorization", CollectionType.LIST, by, "AuthorizationId"},
        };
    }

    @Test (dataProvider = "validLogicalNames")
    public void testValidLogicalNames(
            final String stimulus,
            final VocabularyWord expectedAction,
            final String expectedSubject,
            final CollectionType expectedCollectionType,
            final VocabularyWord expectedPreposition,
            final String expectedObject) throws CoreRouterSemanticsException {

        LogicalNameParser parser = this.applicationSemantics.getLogicalNameParserImpl();
        ParsedName name = parser.parse(stimulus);

        Assert.assertEquals(name.getAction(), expectedAction);
        Assert.assertEquals(name.getSubject(), expectedSubject);
        Assert.assertEquals(name.getCollectionType(), expectedCollectionType);
        Assert.assertEquals(name.getPreposition(), expectedPreposition);
        Assert.assertEquals(name.getObject(), expectedObject);
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

        LogicalNameParser parser = this.applicationSemantics.getLogicalNameParserImpl();
        ParsedName name = parser.parse(stimulus);
    }
}
