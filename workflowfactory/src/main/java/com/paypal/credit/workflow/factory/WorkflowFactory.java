package com.paypal.credit.workflow.factory;

import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutorBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cbeckey on 1/19/16.
 */
@Configuration
@Service (value = "workflowFactory")
public class WorkflowFactory implements ApplicationContextAware {
    /** */
    private final Map<URL, HierarchicalBeanFactory> workflowContexts = new ConcurrentHashMap<>();

    /** */
    private ApplicationContext applicationContext;

    @Bean(name = "defaultParallelProcesorThreadpool")
    RSThreadPoolExecutor defaultThreadPool() {
        RSThreadPoolExecutorBuilder builder = new RSThreadPoolExecutorBuilder();
        builder.withCorePoolSize(10)
                .withKeepAliveTimeSeconds(10)
                .withMaximumPoolSize(100);
        return builder.build();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     *
     * @param workflowDefinition
     * @return
     */
    public Workflow getOrCreate(final URL workflowDefinition) {

        HierarchicalBeanFactory childCtx = workflowContexts.get(workflowDefinition);
        if (childCtx == null) {
            childCtx = new AbstractXmlApplicationContext(applicationContext) {
                @Override
                protected Resource[] getConfigResources() {
                    return new Resource[]{new UrlResource(workflowDefinition)};
                }
            };

            if (childCtx == null) {
                throw new IllegalArgumentException(String.format("Unable to find context definition %s.", workflowDefinition));
            }
            ((AbstractXmlApplicationContext)childCtx).refresh();
            workflowContexts.put(workflowDefinition, childCtx);
        }

        Workflow workflow = (Workflow) childCtx.getBean("workflow");

        return workflow;
    }

}
