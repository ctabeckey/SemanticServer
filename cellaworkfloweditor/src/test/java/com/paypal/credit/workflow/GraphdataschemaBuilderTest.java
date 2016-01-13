package com.paypal.credit.workflow;

import com.paypal.credit.json.Graphdataschema;
import com.paypal.credit.workflowcommand.WorkflowReader;
import com.paypal.credit.workflowcommand.workflow.schema.WorkflowType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

/**
 * Created by cbeckey on 12/22/15.
 */
public class GraphdataschemaBuilderTest {
    @DataProvider
    public Object[][] testData() {
        return new Object[][] {
                new Object[]{"OneStepWorkflow.xml", 3},
                new Object[]{"TwoStepWorkflow.xml", 4}
        };
    }


    @Test(dataProvider = "testData")
    public void test(final String resource, int totalNodes) throws JAXBException {
        GraphdataschemaBuilder builder = new GraphdataschemaBuilder();

        WorkflowType workflow =
            WorkflowReader.create(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));

        builder.withWorkflow(workflow);
        Graphdataschema graph = builder.build();

        Assert.assertNotNull(graph);
        Assert.assertEquals(totalNodes, graph.getElements().getNodes().size());
    }
}
