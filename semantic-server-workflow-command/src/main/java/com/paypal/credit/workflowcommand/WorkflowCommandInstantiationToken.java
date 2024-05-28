package com.paypal.credit.workflowcommand;

import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;
import org.nanocontext.semanticserverapi.core.commandprovider.CommandInstantiationToken;
import org.nanocontext.semanticserverapi.core.commandprovider.CommandProvider;
import com.paypal.credit.workflow.Workflow;

/**
 * Created by cbeckey on 11/17/15.
 */
public class WorkflowCommandInstantiationToken
        implements CommandInstantiationToken {
    private final CommandProvider provider;
    private final Workflow workflow;
    private final RoutingToken routingToken;
    private final Class<?> resultType;

    WorkflowCommandInstantiationToken(final CommandProvider provider,
                                      final Workflow workflow,
                                      final RoutingToken routingToken,
                                      final Class<?> resultType) {
        this.provider = provider;
        this.workflow = workflow;
        this.routingToken = routingToken;
        this.resultType = resultType;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return this.provider;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public RoutingToken getRoutingToken() {
        return routingToken;
    }

    public Class<?> getResultType() {
        return resultType;
    }
}
