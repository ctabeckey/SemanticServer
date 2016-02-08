package com.paypal.credit.workflow.factory;

import com.paypal.credit.context.Context;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.workflow.Workflow;
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
            throws IOException, JAXBException, ContextInitializationException {
        Context ctx = Context.create(contextDefinition);
        return ctx.getBean("workflow", Workflow.class);
    }
}