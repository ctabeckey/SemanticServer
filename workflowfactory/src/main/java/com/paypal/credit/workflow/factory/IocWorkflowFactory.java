package com.paypal.credit.workflow.factory;

import com.paypal.credit.context.Context;
import com.paypal.credit.context.ContextFactory;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.exceptions.WorkflowContextException;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutorBuilder;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by cbeckey on 1/19/16.
 */
public class IocWorkflowFactory {
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
        Context ctx = new ContextFactory()
                .with(contextDefinition)
                .withExternalBeanDefinition(
                        "defaultThreadPool", getDefaultThreadPool()
                )
                .build();

        Workflow workflow = ctx.getBean("workflow", Workflow.class);
        workflow.validate();

        return workflow;
    }
}