package com.paypal.credit.workflow;

/**
 * Created by cbeckey on 12/21/15.
 */
public interface Visitor<N> {
    /**
     * Called when each node in the hierarchy is visited.
     *
     * @param visitedNode the node being visited
     * @return TRUE to continue, FALSE to stop
     */
    boolean visit(N visitedNode);

}
