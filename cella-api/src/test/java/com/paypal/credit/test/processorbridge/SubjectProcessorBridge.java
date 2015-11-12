package com.paypal.credit.test.processorbridge;

import com.paypal.credit.core.processorbridge.ProcessorBridge;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface SubjectProcessorBridge
extends ProcessorBridge {
    /**  */
    AuthorizationId postAuthorization(Authorization authorization);

    /** */
    Authorization getAuthorizationByAuthorizationId(AuthorizationId authorizationId);
}
