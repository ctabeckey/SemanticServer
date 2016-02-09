package com.paypal.credit.processors;

import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflow.subjects.AccountIdProcessorContext;

/**
 * Created by cbeckey on 11/16/15.
 */
@ProvidesGroups({AccountIdProcessorContext.AccountIdValidationGroup.class})
@RequiresGroups({AccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorOne
    extends AbstractProcessor<AccountIdProcessorContext> {

    private final String configOne;
    private final int configTwo;

    public ProcessorOne() {
        this.configOne = "Hello World";
        this.configTwo = 42;
    }

    public ProcessorOne(final String configOne, final Integer configTwo) {
        this.configOne = configOne;
        this.configTwo = configTwo.intValue();
    }

    @Override
    public boolean process(final AccountIdProcessorContext rsProcessorContext) throws RSWorkflowException {

        return super.process(rsProcessorContext);
    }
}
