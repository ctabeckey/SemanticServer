package com.paypal.credit.workflowcommand.factory;

import com.paypal.credit.context.Context;
import com.paypal.credit.context.ContextFactory;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.exceptions.WorkflowContextException;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by cbeckey on 1/19/16.
 */
public class IocWorkflowFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(IocWorkflowFactory.class);
    RSThreadPoolExecutor getDefaultThreadPool() {
        RSThreadPoolExecutorBuilder builder = new RSThreadPoolExecutorBuilder();
        builder.withCorePoolSize(10)
                .withKeepAliveTimeSeconds(10)
                .withMaximumPoolSize(100);
        return builder.build();
    }

    /**
     * @param contextDefinition
     * @return
     */
    public Workflow getOrCreate(final URL contextDefinition)
            throws IOException, JAXBException, ContextInitializationException, WorkflowContextException {
        LOGGER.info("creating command from workflow at [{}]", contextDefinition.toExternalForm());
        Context ctx = new ContextFactory()
                .with(contextDefinition)
                .withExternalBeanDefinition(
                        "defaultThreadPool", getDefaultThreadPool()
                )
                .build();
        LOGGER.info("workflow command resource [{}] loaded", contextDefinition.toExternalForm());

        Workflow workflow = ctx.getBean("workflow", Workflow.class);
        LOGGER.info("workflow command resource [{}], workflow is {}", contextDefinition.toExternalForm(), workflow == null?"NULL":"not null");

        workflow.validate();
        LOGGER.info("workflow command resource [{}] validated", contextDefinition.toExternalForm());

        return workflow;
    }
}