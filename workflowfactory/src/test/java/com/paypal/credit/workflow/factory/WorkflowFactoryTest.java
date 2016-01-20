package com.paypal.credit.workflow.factory;

import com.paypal.credit.utility.URLFactory;
import com.paypal.credit.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cbeckey on 1/19/16.
 */

@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class WorkflowFactoryTest extends AbstractTestNGSpringContextTests {
    private final Logger LOGGER = LoggerFactory.getLogger(WorkflowFactoryTest.class);

    @Inject
    WorkflowFactory workflowFactory;

    @DataProvider
    public Object[][] testWorkflowCreationDataProvider() throws MalformedURLException {
        return new Object[][] {
                new Object[]{"rsc:MultiStepWorkflow.xml"}
        };
    }

    @Test (dataProvider = "testWorkflowCreationDataProvider")
    public void testWorkflowCreation(String workflowLocation) throws MalformedURLException {
        URL workflowUrl = URLFactory.create(workflowLocation);
        Workflow workflow = workflowFactory.getOrCreate(workflowUrl);

        Assert.assertNotNull(workflow);
    }

    @Test (dataProvider = "testWorkflowCreationDataProvider")
    public void canary(String workflowLocation) {
        LOGGER.info("{}", workflowLocation);
        Assert.assertEquals(true, true);
    }
}
