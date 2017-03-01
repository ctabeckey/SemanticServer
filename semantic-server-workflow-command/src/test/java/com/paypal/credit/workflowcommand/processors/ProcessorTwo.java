package com.paypal.credit.workflowcommand.processors;

import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflowcommand.model.AccountIdProcessorContext;

/**
 * Created by cbeckey on 11/16/15.
 */
@RequiresGroups({AccountIdProcessorContext.AccountIdValidationGroup.class,
        AccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorTwo
        extends AbstractProcessor<AccountIdProcessorContext>
{

}
