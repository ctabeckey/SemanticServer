package com.paypal.credit.workflowcommand.processors;

import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflowcommand.SSWCAccountIdProcessorContext;

/**
 * Created by cbeckey on 11/16/15.
 */
@RequiresGroups({SSWCAccountIdProcessorContext.AccountIdValidationGroup.class,
        SSWCAccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorTwoSSWC
        extends SSWCAbstractProcessor<SSWCAccountIdProcessorContext>
{

}
