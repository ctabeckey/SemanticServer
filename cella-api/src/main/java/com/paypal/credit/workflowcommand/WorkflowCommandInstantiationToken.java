package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;

/**
 * Created by cbeckey on 11/17/15.
 */
public class WorkflowCommandInstantiationToken implements CommandInstantiationToken {
    private final CommandProvider provider;
    private final WorkflowType workflowType;

    WorkflowCommandInstantiationToken(final CommandProvider provider, final WorkflowType workflowType) {
        this.provider = provider;
        this.workflowType = workflowType;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return this.provider;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }
}
