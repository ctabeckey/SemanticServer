package com.paypal.credit.workflowcommand.factory;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.exceptions.WorkflowContextException;
import com.paypal.utility.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cbeckey on 1/19/16.
 */

public class WorkflowFactoryTest {
    private final Logger LOGGER = LoggerFactory.getLogger(WorkflowFactoryTest.class);

    private IocWorkflowFactory workflowFactory = new IocWorkflowFactory();

    @DataProvider
    public Object[][] testWorkflowCreationDataProvider() throws MalformedURLException {
        return new Object[][] {
                new Object[]{"rsc:OneStepWorkflow.xml"},
                new Object[]{"rsc:MultiStepWorkflow.xml"},
                new Object[]{"rsc:IocOneStepWorkflow.xml"},
                new Object[]{"rsc:ParallelWorkflow.xml"},
                new Object[]{"rsc:TwoStepWorkflow.xml"}
        };
    }

    @Test (dataProvider = "testWorkflowCreationDataProvider")
    public void testWorkflowCreation(String workflowLocation)
            throws IOException, WorkflowContextException, ContextInitializationException, JAXBException {
        long start = System.currentTimeMillis();
        URL workflowUrl = URLFactory.create(workflowLocation);
        Workflow workflow = workflowFactory.getOrCreate(workflowUrl);
        long end = System.currentTimeMillis();

        System.out.println(String.format("Created %s in %d milliseconds", workflowUrl, end - start));

        Assert.assertNotNull(workflow);
    }

    @Test (dataProvider = "testWorkflowCreationDataProvider")
    public void canaryTest(String workflowLocation) {
        LOGGER.info("{}", workflowLocation);
        Assert.assertEquals(true, true);
    }
}
