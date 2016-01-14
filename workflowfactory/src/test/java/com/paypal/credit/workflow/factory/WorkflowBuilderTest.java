package com.paypal.credit.workflow.factory;

import com.paypal.credit.utility.URLFactory;
import com.paypal.credit.workflow.RSSerialController;
import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflow.exceptions.InvalidWorkflowException;
import com.paypal.credit.workflow.exceptions.WorkflowContextException;
import com.paypal.credit.workflow.schema.WorkflowType;
import com.paypal.credit.workflow.subjects.AccountIdProcessorContext;
import com.paypal.credit.workflow.subjects.AuthorizationId;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cbeckey on 11/16/15.
 */
public class WorkflowBuilderTest {
    @BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    @DataProvider
    public Object[][] validWorkflowDataProvider()
            throws MalformedURLException {
        return new Object[][] {
                new Object[]{
                        new URL("rsc:OneStepWorkflow.xml")
                },
                new Object[]{
                        new URL("rsc:TwoStepWorkflow.xml")
                },
                new Object[]{
                        new URL("rsc:ParallelWorkflow.xml")
                }
        };
    }

    @Test(dataProvider = "validWorkflowDataProvider")
    public void testValidSerialProcessor(final URL workflowLocation)
            throws JAXBException, MalformedURLException, InvalidWorkflowException, RSWorkflowException {
        WorkflowType wf = WorkflowReader.create(workflowLocation);
        Assert.assertNotNull(wf);

        RSSerialController<AccountIdProcessorContext> workflow = new WorkflowBuilder()
                    .withContextClass(AccountIdProcessorContext.class)
                    .withWorkflowType(wf)
                    .build();

        Assert.assertNotNull(workflow);

        AccountIdProcessorContext ctx = new AccountIdProcessorContext(new AuthorizationId("655321"));
        workflow.process(ctx);
    }

    @Test(dataProvider = "validWorkflowDataProvider")
    public void testValidWorkflow(final URL workflowLocation)
            throws JAXBException, MalformedURLException, InvalidWorkflowException, RSWorkflowException {
        WorkflowType wf = WorkflowReader.create(workflowLocation);
        Assert.assertNotNull(wf);

        Class<?> resultType = Void.class;
        Workflow workflow = new WorkflowBuilder()
                .withContextClass(AccountIdProcessorContext.class)
                .withWorkflowType(wf)
                .build();

        Assert.assertNotNull(workflow);
    }

    @DataProvider
    public Object[][] invalidWorkflowDataProvider()
            throws MalformedURLException {
        return new Object[][] {
                new Object[]{
                        new URL("rsc:InvalidTwoStepWorkflow.xml")
                },
        };
    }

    @Test(dataProvider = "invalidWorkflowDataProvider", expectedExceptions = {WorkflowContextException.class})
    public void testInvalidWorkflow(final URL workflowLocation)
            throws JAXBException, MalformedURLException, InvalidWorkflowException, RSWorkflowException, WorkflowContextException {
        WorkflowType wf = WorkflowReader.create(workflowLocation);
        Assert.assertNotNull(wf);

        Class<?> resultType = Void.class;
        Workflow workflow = new WorkflowBuilder()
                .withContextClass(AccountIdProcessorContext.class)
                .withWorkflowType(wf)
                .build();
        workflow.validate();
    }
}
