package com.paypal.credit.processors.test;

import com.paypal.credit.processors.context.TestProcessorContext;
import com.paypal.credit.workflow.annotations.RequiresGroups;

/**
 * Created by cbeckey on 11/16/15.
 */
@RequiresGroups({TestProcessorContext.AccountIdValidationGroup.class,
        TestProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorTwo
        extends ProcessorOne
{
    public ProcessorTwo(final String configOne, final Integer configTwo) {
        super(configOne, configTwo);
    }
}
