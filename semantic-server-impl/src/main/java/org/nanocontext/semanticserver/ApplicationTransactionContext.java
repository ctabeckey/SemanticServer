package org.nanocontext.semanticserver;

import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface ApplicationTransactionContext {
    /** */
    public long getTransactionStartTime();

    /** */
    public RoutingToken getRoutingToken();
}
