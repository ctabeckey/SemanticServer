package com.paypal.credit.workflow;

import com.paypal.credit.workflow.json.Graphdataschema;
import com.paypal.credit.workflowcommand.workflow.schema.BusinessProcessorType;
import com.paypal.credit.workflowcommand.workflow.schema.OptionType;
import com.paypal.credit.workflowcommand.workflow.schema.OptionsType;

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
     * @param visitor the current node being visited
     */
    public boolean visit(final NodeDecoratorVisitor visitor, final int parentX, final int parentY) {
        int childX = parentX;
        int childY = parentY + 1;

        if (!visitor.visit(this, childX, childY)) {
            return false;
        }
        if (this.outgoing != null) {
            for (NodeDecorator node : this.outgoing) {
                if (!node.visit(visitor, childX, childY))
                    return false;
                childX++;
            }
        }

        return true;
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

        private String getNextNodeIdentifier() {
            return String.format("n%s", nodeIndex.getAndIncrement());
        }

        NodeDecorator createSerialStart() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),    // synthetic identifier, for display only
                            null,                       // processor
                            "Start Serial",             // name
                            GraphNodeType.SerialStart.getDisplayKey(),  // type
                            null                        // configuration values
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "serial"                                             // String classes
            );
            return new NodeDecorator(node, GraphNodeType.SerialStart);
        }

        NodeDecorator createSerialEnd() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            "End Serial",
                            GraphNodeType.SerialEnd.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "endserial"                                             // String classes
            );
            return new NodeDecorator(node, GraphNodeType.SerialEnd);
        }

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

        NodeDecorator createConditionalStart() {
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

        NodeDecorator createConditionalEnd() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(
                            getNextNodeIdentifier(),
                            null,
                            "ENDIF",
                            GraphNodeType.ConditionalEnd.getDisplayKey(),
                            null
                    ), // Data data,
                    new Graphdataschema.Elements.Nodetype.Position(new Long(0), new Long(0)),   // Position position,
                    Boolean.FALSE,                                      // Boolean removed,
                    Boolean.FALSE,                                      // Boolean selected,
                    Boolean.FALSE,                                      // Boolean selectable,
                    Boolean.FALSE,                                      // Boolean locked,
                    Boolean.TRUE,                                       // Boolean grabbable,
                    "endif"                                                // String classes
            );
            return new NodeDecorator(node, GraphNodeType.ConditionalEnd);
        }

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
         * @param processor
         * @return
         */
        NodeDecorator createBusinessProcessor(final BusinessProcessorType processor) {
            OptionsType options = processor.getOptions();

            List<Graphdataschema.Elements.Nodetype.Data.ConfigurationItem> configItems = null;
            if (options != null && options.getOption() != null) {
                configItems = new ArrayList<>();
                for (OptionType option : options.getOption()) {
                    Graphdataschema.Elements.Nodetype.Data.ConfigurationItem item =
                            new Graphdataschema.Elements.Nodetype.Data.ConfigurationItem();
                    item.setKey(option.getKey());
                    item.setValue(option.getValue());
                    item.setType(option.getType().value());
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
