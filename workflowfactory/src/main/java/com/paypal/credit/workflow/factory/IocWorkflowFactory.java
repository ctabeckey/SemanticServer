package com.paypal.credit.workflow.factory;

import com.paypal.credit.context.Context;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutorBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

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
        Context context = readContext(contextDefinition);

        return context.getBean("workflow", Workflow.class);
    }

    public Context readContext(final URL workflowDefinition)
            throws IOException, JAXBException, ContextInitializationException {
        InputStream workflowStream = workflowDefinition.openStream();

        return readContext(workflowStream);
    }

    public Context readContext(final InputStream workflowStream)
            throws IOException, JAXBException, ContextInitializationException {
        JAXBContext jc = JAXBContext.newInstance("com.paypal.credit.ioc");

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        BeansType beans = (BeansType) unmarshaller.unmarshal(workflowStream);

        return Context.create(beans);
    }
}