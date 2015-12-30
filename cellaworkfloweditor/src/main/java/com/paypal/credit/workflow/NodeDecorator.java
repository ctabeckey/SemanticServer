package com.paypal.credit.workflow;

import com.paypal.credit.workflow.json.Graphdataschema;
import com.paypal.credit.workflowcommand.workflow.schema.BusinessProcessorType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created by cbeckey on 12/18/15.
 */
public class NodeDecorator
implements Iterable<NodeDecorator>{

    private final Graphdataschema.Elements.Nodetype node;
    private final GraphNodeType graphNodeType;
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
    public boolean visit(NodeDecoratorVisitor visitor) {
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

        public Long resetX() {
            xIndex.set(0);
            return new Long(0);
        }
        public Long getAndIncrementX() {
            return new Long(xIndex.getAndIncrement());
        }
        public Long incrementAndGetX() {
            return new Long(xIndex.incrementAndGet());
        }
        private Long getX() {
            return new Long(xIndex.longValue());
        }

        public Long resetY() {
            yIndex.set(0);
            return new Long(0);
        }
        public Long getAndIncrementY() {
            return new Long(yIndex.getAndIncrement());
        }
        public Long incrementAndGetY() {
            return new Long(yIndex.incrementAndGet());
        }
        private Long getY() {
            return new Long(yIndex.longValue());
        }

        NodeDecorator createSerialStart() {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "SERIAL"), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "ENDSERIAL"), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "FORK"), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "JOIN"), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "IF"), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "ENDIF"), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), contextClassName), // Data data,
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
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), "END"), // Data data,
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

        NodeDecorator createBusinessProcessor(final BusinessProcessorType processor) {
            Graphdataschema.Elements.Nodetype node = new Graphdataschema.Elements.Nodetype(
                    new Graphdataschema.Elements.Nodetype.Data(getNextNodeIdentifier(), new Long(1), processor.getImpl()), // Data data,
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

        public Set<Graphdataschema.Elements.Edgetype> createOutgoingEdges(NodeDecorator root) {
            Set<Graphdataschema.Elements.Edgetype> edges = new HashSet<>();

            // iterate the outgoing connections of the root
            for (NodeDecorator node : root) {
                String source = root.getNode().getData().getId();
                String target = node.getNode().getData().getId();

                Graphdataschema.Elements.Edgetype edge = new Graphdataschema.Elements.Edgetype(
                        new Graphdataschema.Elements.Edgetype.Data(getNextEdgeIdentifier(root), new Long(1), source, target),
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
