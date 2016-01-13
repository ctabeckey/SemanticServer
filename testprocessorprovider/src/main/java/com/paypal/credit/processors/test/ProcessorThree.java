package com.paypal.credit.processors.test;

import com.paypal.credit.processors.context.TestProcessorContext;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;

/**
 * Created by cbeckey on 11/16/15.
 */
@ProvidesGroups({TestProcessorContext.AccountIdValidationGroup.class})
@RequiresGroups({TestProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorThree
        extends AbstractProcessor<RSProcessorContext>
{

}
