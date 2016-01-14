package com.paypal.credit.workflow;

import com.paypal.credit.json.Graphdataschema;
import com.paypal.credit.workflow.schema.BusinessProcessorType;
import com.paypal.credit.workflow.schema.OptionType;
import com.paypal.credit.workflow.schema.OptionsType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 *
 */
public class NodeDecorator
implements Iterable<NodeDecorator>{

    // this is the VO that is serialized and sent to the client
    private final Graphdataschema.Elements.Nodetype node;
    // one of: business, parallel, serial, conditional
    private final GraphNodeType graphNodeType;
    // outgoing connections to other downstream nodes
    private final Set<NodeDecorator> outgoing = new HashSet<>();

    private NodeDecorator(final Graphdataschema.Elements.Nodetype node, final GraphNodeType graphNodeType) {
        this.node = node;
        this.graphNodeType = graphNodeType;
    }

    public Graphdataschema.Elements.Nodetype getNode() {
        return node;
    }

    public GraphNodeType getGraphNodeType() {
        return graphNodeType;
    }

    public boolean addOutgoingConnection(final NodeDecorator destination) {
        return destination == null ? false : this.outgoing.add(destination);
    }
    public boolean addAllOutgoing(final Collection<NodeDecorator> destinations) {
        boolean result = false;
        if (destinations != null) {
            for (NodeDecorator nodeDecorator : destinations) {
                result |= addOutgoingConnection(nodeDecorator);
            }
        }

        return result;
    }

    @Override
    public void forEach(final Consumer<? super NodeDecorator> action) {
        this.outgoing.forEach(action);
    }

    @Override
    public Spliterator<NodeDecorator> spliterator() {
        return this.outgoing.spliterator();
    }

    @Override
    public Iterator<NodeDecorator> iterator() {
        return this.outgoing.iterator();
    }

    /**
     *
     * @return TRUE if the sub-tree was completely visited, FALSE if the visitor returned FALSE anywhere
     * @param visitor the vistor to be applied to the node
     */
    public boolean visit(final Visitor<NodeDecorator> visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        if (this.outgoing != null) {
            for (NodeDecorator node : this.outgoing) {
                if (!node.visit(visitor))
                    return false;
            }
        }

        return true;
    }

    /**
     * A different visitor that generates hints to the client
     * layout algorithm.
     */
    public void calculateLayoutHints(final LayoutHint parentLayoutHint) {
        int childX = parentLayoutHint.getX();
        int childY = parentLayoutHint.getY() + 1;

        node.getPosition().setX(new Long(childX));
        node.getPosition().setY(new Long(childY));


        if (this.outgoing != null) {
            int count = this.outgoing.size();
            int index = -(count / 2);
            for (NodeDecorator node : this.outgoing) {
                childX = parentLayoutHint.getX() + index;
                node.calculateLayoutHints(new LayoutHint(childX, childY));
                ++index;
            }
        }
    }

    /**
     *
     * @return
     */
    public LayoutHint calculateLayoutSize() {
        int maxChildDepth = 0;
        int maxChildCount = this.childCount();

        for (NodeDecorator child : this) {
            LayoutHint childLayoutHint = child.calculateLayoutSize();
            maxChildDepth = Math.max(maxChildDepth, childLayoutHint.getY());
            maxChildCount = Math.max(maxChildCount, childLayoutHint.getX());
        }

        return new LayoutHint(maxChildCount, maxChildDepth + 1);
    }

    /**
     *
     */
    public static class LayoutHint {
        private final int x;
        private final int y;

        LayoutHint(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        /**
         * Add the X and Y coordinates and return a new LayoutHint with the sum.
         * @param that
         * @return
         */
        public LayoutHint add(LayoutHint that) {
            return new LayoutHint(this.getX() + that.getX(), this.getY() + that.getY());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @return the number of outgoing connections
     *   -or-
     * -1 if the number has not been determined yet
     */
    public int childCount() {
        return this.outgoing == null ? -1 : this.outgoing.size();
    }

    static class Builder {
        private AtomicInteger edgeIndex = new AtomicInteger(0);
        private String getNextEdgeIdentifier(NodeDecorator root) {
            return String.format("e%s%d", root.getNode().getData().getId(), edgeIndex.getAndIncrement());
        }

        private AtomicInteger nodeIndex = new AtomicInteger(0);
        private AtomicInteger xIndex = new AtomicInteger(0);
        private AtomicInteger yIndex = new AtomicInteger(0);

        private Builder() {}

        private String getNextNodeIdentifier() {
            return String.format("n%s", nodeIndex.getAndIncrement());
        }

        /**
         *
         * @param contextClassName
         * @return
         */
        NodeDecorator createGraphStart(final String contextClassName) {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            contextClassName,
                            GraphNodeType.Start.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "start"                                                // String classes
            );
            return new NodeDecorator(node, GraphNodeType.Start);
        }

        /**
         *
         * @return
         */
        NodeDecorator createGraphEnd() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            "END",
                            GraphNodeType.End.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "end"                                                // String classes
            );
            return new NodeDecorator(node, GraphNodeType.End);
        }

        /**
         *
         * @return
         */
        NodeDecorator createParallelStart() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            "FORK",
                            GraphNodeType.ParallelStart.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "fork"                                              // String classes
            );
            return new NodeDecorator(node, GraphNodeType.ParallelStart);
        }

        /**
         *
         * @return
         */
        NodeDecorator createParallelEnd() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            "JOIN",
                            GraphNodeType.ParallelEnd.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "join"                                              // String classes
            );
            return new NodeDecorator(node, GraphNodeType.ParallelEnd);
        }

        /**
         *
         * @return
         */
        NodeDecorator createConditional() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            "IF",
                            GraphNodeType.ConditionalStart.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "if"                                                // String classes
            );
            return new NodeDecorator(node, GraphNodeType.ConditionalStart);
        }

        /**
         *
         * @param processor
         * @return
         */
        NodeDecorator createBusinessProcessor(final BusinessProcessorType processor) {
            OptionsType options = processor.getOptions();

            List<Graphdataschema.Elements.Nodetype.Data.PropertyValue> configItems = null;
            if (options != null && options.getOption() != null) {
                configItems = new ArrayList<>();
                for (OptionType option : options.getOption()) {
                    Graphdataschema.Elements.Nodetype.Data.PropertyValue item =
                            new Graphdataschema.Elements.Nodetype.Data.PropertyValue();
                    item.setKey(option.getKey());
                    item.setValue(option.getValue());
                }
            }

            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            processor.getImpl(),
                            "",
                            GraphNodeType.Business.getDisplayKey(),
                            configItems
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "business"                                          // String classes
            );
            return new NodeDecorator(node, GraphNodeType.Business);
        }

        /**
         *
         * @param root
         * @return
         */
        public Set<Graphdataschema.Elements.Edgetype> createOutgoingEdges(NodeDecorator root) {
            Set<Graphdataschema.Elements.Edgetype> edges = new HashSet<>();

            // iterate the outgoing connections of the root
            for (NodeDecorator node : root) {
                String source = root.getNode().getData().getId();
                String target = node.getNode().getData().getId();

                Graphdataschema.Elements.Edgetype edge = new Graphdataschema.Elements.Edgetype(
                        new Graphdataschema.Elements.Edgetype.Data(
                                getNextEdgeIdentifier(root), new Long(1), source, target
                        ),
                        Boolean.FALSE,      // Boolean removed,
                        Boolean.FALSE,      // Boolean selected,
                        Boolean.TRUE,       // Boolean selectable,
                        Boolean.FALSE,      // Boolean locked,
                        Boolean.TRUE,       // Boolean grabbable,
                        ""                  // String classes
                );
                edges.add(edge);
            }
            return edges;
        }
    }
}
