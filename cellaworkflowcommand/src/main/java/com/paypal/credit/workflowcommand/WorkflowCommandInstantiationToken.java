package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.CommandInstantiationToken;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.workflowcommand.workflow.schema.WorkflowType;

/**
 * Created by cbeckey on 11/17/15.
 */
public class WorkflowCommandInstantiationToken
        implements CommandInstantiationToken {
    private final CommandProvider provider;
    private final WorkflowType workflowType;
    private final RoutingToken routingToken;
    private final Class<?> resultType;

    WorkflowCommandInstantiationToken(final CommandProvider provider,
                                      final WorkflowType workflowType,
                                      final RoutingToken routingToken,
                                      final Class<?> resultType) {
        this.provider = provider;
        this.workflowType = workflowType;
        this.routingToken = routingToken;
        this.resultType = resultType;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return this.provider;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public RoutingToken getRoutingToken() {
        return routingToken;
    }

    public Class<?> getResultType() {
        return resultType;
    }
}
