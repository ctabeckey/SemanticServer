package com.paypal.credit;

import com.paypal.credit.workflow.factory.WorkflowFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cbeckey on 1/28/16.
 */
@Configuration
public class TestConfiguration {
    @Bean
    public WorkflowFactory workflowFactory() {
        return new WorkflowFactory();
    }

}
