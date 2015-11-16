package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.utility.URLFactory;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.RSSerialController;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflowcommand.exceptions.InvalidWorkflowException;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;
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
public class WorkflowFactoryTest {
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
    public void test(final URL workflowLocation)
            throws JAXBException, MalformedURLException, InvalidWorkflowException, RSWorkflowException {
        WorkflowType wf = WorkflowReader.create(workflowLocation);
        Assert.assertNotNull(wf);

        RSSerialController<RSProcessorContext> workflow =
                WorkflowFactory.create(RSProcessorContext.class, wf);

        Assert.assertNotNull(workflow);

        RSProcessorContext ctx = new RSProcessorContext();
        workflow.process(ctx);
    }
}
