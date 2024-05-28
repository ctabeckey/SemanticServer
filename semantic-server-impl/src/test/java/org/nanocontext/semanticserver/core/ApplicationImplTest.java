package org.nanocontext.semanticserver.core;

import org.nanocontext.semanticserver.ApplicationImpl;
import org.nanocontext.semanticserverapi.core.Application;
import org.nanocontext.semanticserverapi.core.semantics.*;
import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

import static org.testng.Assert.assertEquals;

/**
 * Created by cbeckey on 2/3/17.
 */
public class ApplicationImplTest {
    @Test
    public void testBaseApplicationCreation()
            throws FileNotFoundException, JAXBException, ContextInitializationException {
        Application baseApplication = ApplicationImpl.create(true);

        Assert.assertNotNull(baseApplication);

        Assert.assertNotNull(baseApplication.getApplicationSemantics());
        Assert.assertNotNull(baseApplication.getClassLoader());
        Assert.assertNotNull(baseApplication.getCommandProcessor());
        Assert.assertNotNull(baseApplication.getContext());
        Assert.assertNotNull(baseApplication.getRootCommandProvider());
        Assert.assertNotNull(baseApplication.getServiceProvider());
    }

    /**

    /**
     *
     */
    public static class CollectionTypeVocabularyTest
    {

        /**
         * Test method for
         */
        @Test
        public void testGetSimpleName()
        {
            assertEquals("List", CollectionType.LIST.getSimpleName() );
            assertEquals("Set", CollectionType.SET.getSimpleName() );
            assertEquals("Map", CollectionType.MAP.getSimpleName() );
        }

        /**
         * Test method for
         */
        @Test
        public void testFindByObjectSuffix()
        {
            assertEquals(CollectionType.findByObjectSuffix("List"), CollectionType.LIST );
            assertEquals(CollectionType.findByObjectSuffix("Set"), CollectionType.SET );
            assertEquals(CollectionType.findByObjectSuffix("Map"), CollectionType.MAP );
        }
    }

    /**
     *
     */
    public static class CommandClassSemanticsTest
    {
        private ApplicationSemantics applicationSemantics;

        //@BeforeTest
        public void beforeTest() throws CoreRouterSemanticsException {
            this.applicationSemantics = org.nanocontext.semanticserver.semanticserver.semantics.ApplicationSemanticsImpl.create("com.paypal.semanticserver.testValidWorkflow.model");
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

    /**

     /* *
     *
     */
    public static class CommandFactoryMethodSemanticsTest
    {
        private ApplicationSemantics applicationSemantics;

        @BeforeTest
        public void beforeTest() throws CoreRouterSemanticsException {
            this.applicationSemantics = org.nanocontext.semanticserver.semanticserver.semantics.ApplicationSemanticsImpl.create("com.paypal.semanticserver.testValidWorkflow.model");
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
            VocabularyWord get = this.applicationSemantics.getActionVocabulary().find("Get");
            VocabularyWord post = this.applicationSemantics.getActionVocabulary().find("Post");


            return new Object[][]{
                    new Object[]{"createGetAccountCommand", get, "Account", null, null, null},
                    new Object[]{"createGetAccountListCommand", get, "Account", CollectionType.LIST, null, null},
                    new Object[]{"createPostAuthorizationCommand", post, "Authorization", null, null, null},
            };
        };

        @Test(dataProvider = "validElementsData")
        public void testKnownValidElements(
                String methodName,
                VocabularyWord action,
                String subject,
                CollectionType subjectCollectionType,
                VocabularyWord preposition,
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

    /**
     * Created by cbeckey on 11/10/15.
     */
    public static class LogicalNameParserImplTest {
        private ApplicationSemantics applicationSemantics;

        @BeforeTest
        public void beforeTest() throws CoreRouterSemanticsException {
            this.applicationSemantics = org.nanocontext.semanticserver.semanticserver.semantics.ApplicationSemanticsImpl.create("com.paypal.semanticserver.testValidWorkflow.model");
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

            assertEquals(name.getAction(), expectedAction);
            assertEquals(name.getSubject(), expectedSubject);
            assertEquals(name.getCollectionType(), expectedCollectionType);
            assertEquals(name.getPreposition(), expectedPreposition);
            assertEquals(name.getObject(), expectedObject);
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

    /**
     *
     */
    public static class ProcessorBridgeMethodSemanticsTest
    {
        private ApplicationSemantics applicationSemantics;

        @BeforeTest
        public void beforeTest() throws CoreRouterSemanticsException {
            this.applicationSemantics = org.nanocontext.semanticserver.semanticserver.semantics.ApplicationSemanticsImpl.create("com.paypal.semanticserver.testValidWorkflow.model");
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
}
