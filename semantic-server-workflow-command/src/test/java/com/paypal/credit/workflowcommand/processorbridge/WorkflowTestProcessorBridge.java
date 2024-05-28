package com.paypal.credit.workflowcommand.processorbridge;

import org.nanocontext.semanticserverapi.core.commandprocessor.AsynchronousExecutionCallback;
import org.nanocontext.semanticserver.semanticserver.applicationbridge.AsynchronousExecution;
import com.paypal.credit.workflowcommand.model.Authorization;
import com.paypal.credit.workflowcommand.model.AuthorizationId;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface WorkflowTestProcessorBridge {
    /**  */
    Authorization getAuthorization(AuthorizationId authorizationId);

    /** delete the Authorization, running asynchronously */
    @AsynchronousExecution
    void deleteAuthorization(AuthorizationId authorizationId);

    @AsynchronousExecution
    Authorization getAuthorization(
            AsynchronousExecutionCallback<Authorization> callback,
            AuthorizationId authorizationId
    );

}
