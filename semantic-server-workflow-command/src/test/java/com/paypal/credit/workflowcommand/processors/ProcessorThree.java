package com.paypal.credit.workflowcommand.processors;

import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflowcommand.model.AccountIdProcessorContext;

/**
 * Created by cbeckey on 11/16/15.
 */
@ProvidesGroups({AccountIdProcessorContext.AccountIdValidationGroup.class})
@RequiresGroups({AccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorThree
        extends AbstractProcessor<RSProcessorContext>
{

}
