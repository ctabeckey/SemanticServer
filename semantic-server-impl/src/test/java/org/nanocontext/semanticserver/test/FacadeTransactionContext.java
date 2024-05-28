package org.nanocontext.semanticserver.test;

import org.nanocontext.semanticserver.ApplicationTransactionContext;
import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface FacadeTransactionContext
        extends ApplicationTransactionContext {

    void setTransactionStartTime(long startTime);

    void setRoutingToken(RoutingToken routingToken);
}
