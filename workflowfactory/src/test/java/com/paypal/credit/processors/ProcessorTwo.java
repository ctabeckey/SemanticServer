package com.paypal.credit.processors;

import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflow.subjects.AccountIdProcessorContext;

/**
 * Created by cbeckey on 11/16/15.
 */
@RequiresGroups({AccountIdProcessorContext.AccountIdValidationGroup.class,
        AccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorTwo
        extends AbstractProcessor<AccountIdProcessorContext>
{

}
