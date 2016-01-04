package com.paypal.credit.workflow;

/**
 * Created by cbeckey on 12/21/15.
 */
public interface NodeDecoratorVisitor {
    /**
     * Called when each node in the hoerarchy is visited.
     *
     * @param nodeDecorator the node being visited
     * @return TRUE to continue, FALSE to stop
     */
    boolean visit(NodeDecorator nodeDecorator, int parentX, int parentY);

}
