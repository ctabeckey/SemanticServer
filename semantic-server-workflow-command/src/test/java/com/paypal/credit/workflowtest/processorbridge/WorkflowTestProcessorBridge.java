package com.paypal.credit.workflowtest.processorbridge;

import com.paypal.credit.core.commandprocessor.AsynchronousExecutionCallback;
import com.paypal.credit.core.processorbridge.AsynchronousExecution;
import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;

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
