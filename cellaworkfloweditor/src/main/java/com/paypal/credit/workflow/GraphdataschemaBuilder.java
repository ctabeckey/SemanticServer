package com.paypal.credit.workflow;

import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.json.Graphdataschema;
import com.paypal.credit.workflowcommand.workflow.schema.BaseProcessorType;
import com.paypal.credit.workflowcommand.workflow.schema.BusinessProcessorType;
import com.paypal.credit.workflowcommand.workflow.schema.ConditionalControllerType;
import com.paypal.credit.workflowcommand.workflow.schema.ParallelControllerType;
import com.paypal.credit.workflowcommand.workflow.schema.SerialControllerType;
import com.paypal.credit.workflowcommand.workflow.schema.WorkflowType;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Builds the graph for the UI from the internal workflow definition
 */
public class GraphdataschemaBuilder {
    private NodeDecorator.Builder nodeBuilder = new NodeDecorator.Builder();
    private WorkflowType workflow = null;

    public GraphdataschemaBuilder() {
    }

    public GraphdataschemaBuilder withWorkflow(final WorkflowType workflow) {
        this.workflow = workflow;
        return this;
    }

    /**
     *
     * @return
     */
    public Graphdataschema build() {
        ParameterCheckUtility.checkParameterNotNull(this.workflow, "workflow");

        String contextClass = workflow.getContextClass();

        NodeDecorator startNd = buildNodeDecoratorGraph(contextClass);

        List<Graphdataschema.Elements.Nodetype> nodes = new ArrayList<>();
        List<Graphdataschema.Elements.Edgetype> edges = new ArrayList<>();
        startNd.visit(new NodeDecoratorVisitor() {
            @Override
            public boolean visit(final NodeDecorator nodeDecorator) {
                Graphdataschema.Elements.Nodetype node = nodeDecorator.getNode();

                nodes.add(node);
                edges.addAll(nodeBuilder.createOutgoingEdges(nodeDecorator));

                return true;
            }
        });

        Graphdataschema.Elements elements = new Graphdataschema.Elements(nodes, edges);
        Graphdataschema result = new Graphdataschema();
        result.setElements(elements);

        return result;
    }

    protected NodeDecorator buildNodeDecoratorGraph(final String contextClass) {
        NodeDecorator startNd = nodeBuilder.createGraphStart(contextClass);
        NodeDecorator previousNd = startNd;
        for (JAXBElement<? extends BaseProcessorType> processorElement : workflow.getProcessList().getProcessor()) {
            NodeDecorator nd = createNodeDecorator(null, processorElement);

            previousNd.addOutgoingConnection(nd);

            previousNd = findTail(nd);
        }
        NodeDecorator endNd = nodeBuilder.createGraphEnd();
        previousNd.addOutgoingConnection(endNd);

        return startNd;
    }

    /**
     *
     * @param processors
     * @return
     */
    NodeDecorator createSerialNodeDecorators(NodeDecorator root, List<JAXBElement<? extends BaseProcessorType>> processors) {
        if (processors == null) {
            throw new IllegalArgumentException("'processors' is null and must not be");
        }

        NodeDecorator startNode = null;
        NodeDecorator previousNode = null;

        for (JAXBElement<? extends BaseProcessorType> processorElement : processors) {
            NodeDecorator nd = createNodeDecorator(root, processorElement);

            if (startNode == null) {
                startNode = nd;
                previousNode = nd;
            } else {
                previousNode.addOutgoingConnection(nd);
                previousNode = nd;
            }
        }

        return startNode;
    }

    /**
     *
     * @param processors
     * @return the head of the parallel processing chain, or null if no chain was created
     */
    NodeDecorator createParallelNodeDecorators(NodeDecorator root, List<JAXBElement<? extends BaseProcessorType>> processors) {
        if (processors == null) {
            throw new IllegalArgumentException("'processors' is null and must not be");
        }

        NodeDecorator startNode = nodeBuilder.createParallelStart();

        for (JAXBElement<? extends BaseProcessorType> processorElement : processors) {
            NodeDecorator nd = createNodeDecorator(root, processorElement);
            startNode.addOutgoingConnection(nd);
        }

        NodeDecorator endNode = nodeBuilder.createParallelEnd();

        for (NodeDecorator childNode : startNode) {
            childNode.addOutgoingConnection(endNode);
        }

        return startNode;
    }

    /**
     *
     * @param conditionalProcessor
     * @return
     */
    NodeDecorator createConditionalNodeDecorators(NodeDecorator root, ConditionalControllerType conditionalProcessor) {
        if (conditionalProcessor == null) {
            throw new IllegalArgumentException("'conditionalProcessor' is null and must not be");
        }

        NodeDecorator startNode = null;

        JAXBElement<? extends BaseProcessorType> processorElement = conditionalProcessor.getProcessor();
        if (processorElement != null) {
            startNode = nodeBuilder.createConditionalStart();
            NodeDecorator processingNode = createNodeDecorator(root, processorElement.getValue());
            startNode.addOutgoingConnection(processingNode);

            NodeDecorator endNode = nodeBuilder.createConditionalEnd();
            processingNode.addOutgoingConnection(endNode);
        }

        return startNode;
    }

    /**
     *
     * @param root
     * @param businessProcessor
     * @return
     */
    NodeDecorator createBusinessProcessNodeDecorator(NodeDecorator root, BusinessProcessorType businessProcessor) {
        if (businessProcessor == null) {
            throw new IllegalArgumentException("'businessProcessor' is null and must not be");
        }

        NodeDecorator startNode = null;

        startNode = nodeBuilder.createBusinessProcessor(businessProcessor);

        return startNode;
    }

    /**
     *
     * @param processorElement
     * @return
     */
    NodeDecorator createNodeDecorator(NodeDecorator root, JAXBElement<? extends BaseProcessorType> processorElement) {
        if (processorElement == null) {
            throw new IllegalArgumentException("'processorElement' is null and must not be");
        }

        return createNodeDecorator(root, processorElement.getValue());
    }

    /**
     *
     * @param processor
     * @return
     */
    NodeDecorator createNodeDecorator(NodeDecorator root, BaseProcessorType processor) {
        Class<? extends BaseProcessorType> declaredProcessorType = processor.getClass();
        NodeDecorator result = null;

        if (BusinessProcessorType.class == declaredProcessorType) {
            result = createBusinessProcessNodeDecorator(root, (BusinessProcessorType)processor);
        }
        else if (ConditionalControllerType.class == declaredProcessorType) {
            result = createConditionalNodeDecorators(root, (ConditionalControllerType)processor);
        }
        else if (ParallelControllerType.class == declaredProcessorType) {
            result = createParallelNodeDecorators(root, ((ParallelControllerType)processor).getProcessor());
        }
        else if (SerialControllerType.class == declaredProcessorType) {
            result = createSerialNodeDecorators(root, ((SerialControllerType)processor).getProcessor());
        }
        else {
            throw new IllegalArgumentException(declaredProcessorType.getName() + " is not a known processor type.");
        }

        return result;
    }

    /**
     * Simple recursive function to find the last NodeDecorator in a chain.
     * This function assumes that there is only one tail and that all paths through
     * the chain end at the tail. The chains created by this Builder will always follow this rule.
     *
     * @param head the start of the chain
     * @return the tail of the chain
     */
    NodeDecorator findTail(NodeDecorator head) {
        if (head == null) {
            return null;
        }
        Iterator<NodeDecorator> iter = head.iterator();
        return iter.hasNext() ? findTail(iter.next()) : head;
    }
}