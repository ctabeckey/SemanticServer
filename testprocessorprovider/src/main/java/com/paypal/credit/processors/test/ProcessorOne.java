package com.paypal.credit.processors.test;

import com.paypal.credit.processors.context.TestProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;

/**
 * Created by cbeckey on 11/16/15.
 */
@ProvidesGroups({TestProcessorContext.AccountIdValidationGroup.class})
@RequiresGroups({TestProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorOne
    extends AbstractProcessor<TestProcessorContext> {

    private final String configOne;
    private final Integer configTwo;

    /**
     * Configuration is only done in the constructor
     *
     * @param configOne
     * @param configTwo
     */
    public ProcessorOne(final String configOne, final Integer configTwo) {
        this.configOne = configOne;
        this.configTwo = configTwo;
    }

    public String getConfigOne() {
        return configOne;
    }

    public Integer getConfigTwo() {
        return configTwo;
    }

    @Override
    public boolean process(final TestProcessorContext rsProcessorContext) throws RSWorkflowException {
        return super.process(rsProcessorContext);
    }
}
