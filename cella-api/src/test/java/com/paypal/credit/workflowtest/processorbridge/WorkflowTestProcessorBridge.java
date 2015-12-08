package com.paypal.credit.workflowtest.processorbridge;

import com.paypal.credit.core.processorbridge.ProcessorBridge;
import com.paypal.credit.workflowcommand.WorkflowContextMapping;
import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface WorkflowTestProcessorBridge
extends ProcessorBridge {
    /**  */
    Authorization getAuthorization(@WorkflowContextMapping("authorizationId") AuthorizationId authorizationId);

    /** */
    Authorization deleteAuthorization(@WorkflowContextMapping("authorizationId") AuthorizationId authorizationId);
}
