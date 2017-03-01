package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.ApplicationTransactionContext;
import com.paypal.credit.core.commandprocessor.RoutingToken;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface FacadeTransactionContext
        extends ApplicationTransactionContext {

    void setTransactionStartTime(long startTime);

    void setRoutingToken(RoutingToken routingToken);
}
