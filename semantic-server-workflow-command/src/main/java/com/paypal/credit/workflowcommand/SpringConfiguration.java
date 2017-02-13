package com.paypal.credit.workflowcommand;

import com.paypal.credit.workflow.factory.IocWorkflowFactory;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cbeckey on 1/28/16.
 */
@Configuration
public class SpringConfiguration
        implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean(name = "defaultThreadPool")
    public RSThreadPoolExecutor defaultThreadPool() {
        return RSThreadPoolExecutor.getBuilder()
                .withCorePoolSize(10)
                .withMaximumPoolSize(100)
                .withKeepAliveTimeSeconds(10)
                .build();
    }

    @Bean(name="workflowFactory")
    public IocWorkflowFactory workflowFactory() {
        return new IocWorkflowFactory();
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
