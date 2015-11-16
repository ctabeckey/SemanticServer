package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.utility.URLFactory;
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
public class WorkflowReaderTest {

    @BeforeClass
    public static void initializeHandlers() {
        URLFactory.registerHandlers();
    }

    @DataProvider
    public Object[][] validWorkflowDataProvider()
            throws MalformedURLException {
        return new Object[][] {
                new Object[]{
                        new URL("rsc:OneStepWorkflow.xml"), 1
                },
                new Object[]{
                        new URL("rsc:TwoStepWorkflow.xml"), 2
                },
                new Object[]{
                        new URL("rsc:ParallelWorkflow.xml"), 1
                },
                new Object[]{
                        new URL("rsc:workflow1.xml"), 1
                },
        };
    }

    @Test(dataProvider = "validWorkflowDataProvider")
    public void test(final URL workflowLocation, final int processorCount)
            throws JAXBException, MalformedURLException {
        WorkflowType wf = WorkflowReader.create(workflowLocation);
        Assert.assertNotNull(wf);
        Assert.assertNotNull(wf.getStart());
        Assert.assertNotNull(wf.getStart().getParallelOrSerialOrConditional());
        Assert.assertEquals(wf.getStart().getParallelOrSerialOrConditional().size(), processorCount);
    }
}
