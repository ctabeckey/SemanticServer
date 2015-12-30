package com.paypal.credit.processors;

import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflowcommand.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflowcommand.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflowtest.AccountIdProcessorContext;

import javax.validation.Valid;

/**
 * Created by cbeckey on 11/16/15.
 */
@ProvidesGroups({AccountIdProcessorContext.AccountIdValidationGroup.class})
@RequiresGroups({AccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class ProcessorOne
    extends AbstractProcessor<AccountIdProcessorContext> {

    @Override
    public boolean process(final AccountIdProcessorContext rsProcessorContext) throws RSWorkflowException {

        return super.process(rsProcessorContext);
    }
}
