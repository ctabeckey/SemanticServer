package com.paypal.credit.test.processorbridge;

import com.paypal.credit.core.processorbridge.ProcessorBridge;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;
import com.paypal.credit.workflowcommand.WorkflowContextMapping;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface SubjectProcessorBridge
extends ProcessorBridge {
    /**  */
    AuthorizationId postAuthorization(
            @WorkflowContextMapping("authorization") Authorization authorization
    );

    /** */
    Authorization getAuthorizationByAuthorizationId(
            @WorkflowContextMapping("authorizationId") AuthorizationId authorizationId
    );
}
