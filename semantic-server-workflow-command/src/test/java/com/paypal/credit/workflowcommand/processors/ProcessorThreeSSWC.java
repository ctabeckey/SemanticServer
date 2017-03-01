package com.paypal.credit.workflowcommand.processors;

import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflowcommand.SSWCAccountIdProcessorContext;

/**
 * Created by cbeckey on 11/16/15.
 */
@ProvidesGroups({SSWCAccountIdProcessorContext.AccountIdValidationGroup.class})
@RequiresGroups({SSWCAccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorThreeSSWC
        extends SSWCAbstractProcessor<RSProcessorContext>
{

}
