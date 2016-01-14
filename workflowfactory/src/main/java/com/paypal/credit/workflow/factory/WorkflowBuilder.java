package com.paypal.credit.workflow.factory;

import com.paypal.credit.workflow.Workflow;
import com.paypal.credit.workflow.exceptions.InvalidWorkflowDefinitionException;
import com.paypal.credit.workflow.exceptions.InvalidWorkflowException;
import com.paypal.credit.workflow.exceptions.InvalidWorkflowProcessorException;
import com.paypal.credit.workflow.exceptions.UnableToInstantiateWorkflowProcessorException;
import com.paypal.credit.workflow.RSConditionalController;
import com.paypal.credit.workflow.RSParallelController;
import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.RSSerialController;
import com.paypal.credit.workflow.schema.BaseProcessorType;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import com.paypal.credit.workflow.exceptions.UnknownWorkflowProcessorException;
import com.paypal.credit.workflow.schema.BusinessProcessorType;
import com.paypal.credit.workflow.schema.ConditionalControllerType;
import com.paypal.credit.workflow.schema.ParallelControllerType;
import com.paypal.credit.workflow.schema.ProcessList;
import com.paypal.credit.workflow.schema.SerialControllerType;
import com.paypal.credit.workflow.schema.WorkflowType;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbeckey on 11/13/15.
 */
public class WorkflowBuilder<C extends RSProcessorContext, R> {
    private Class<C> contextClass;
    private WorkflowType workflowType;
    private RSThreadPoolExecutor parallelExecutor;

    /**
     *
     */
    public WorkflowBuilder()
    {}

    public WorkflowBuilder<C, R> withContextClass(final Class<C> contextClass) {
        this.contextClass = contextClass;
        return this;
    }

    public WorkflowBuilder<C, R> withWorkflowType(final WorkflowType workflowType) {
        this.workflowType = workflowType;
        return this;
    }

    public WorkflowBuilder<C, R> withParallelExecutor(final RSThreadPoolExecutor parallelExecutor) {
        this.parallelExecutor = parallelExecutor;
        return this;
    }

    private Class<C> getContextClass() {
        return contextClass;
    }

    private WorkflowType getWorkflowType() {
        return workflowType;
    }

    private RSThreadPoolExecutor getParallelExecutor() {
        if (this.parallelExecutor == null) {
            this.parallelExecutor = RSThreadPoolExecutor.getBuilder().build();
        }
        return parallelExecutor;
    }

    /**
     * @return
     * @throws InvalidWorkflowException
     */
    public <C extends RSProcessorContext> Workflow build()
            throws InvalidWorkflowException {
        ProcessList processes = getWorkflowType().getProcessList();
        List<RSProcessor<RSProcessorContext>> processorList = createProcessorList(processes.getProcessor());

        Workflow wf = new Workflow(processorList, getContextClass());
        return wf;
    }

    /**
     * The generic createProcess method determines the type of the
     * processor and then calls the type specific create() overload.
     * NOTE: The SerialController may NOT be represented explicitly in the
     * XML representation of a workflow. Any sequence of processors that is
     * not contained within a controller type is assumed to be executed serially,
     * and is contained in a SerialController.
     *
     * @param processorElement
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private <T extends RSProcessorContext> RSProcessor<T> createProcessor(
            final JAXBElement<? extends BaseProcessorType> processorElement)
            throws InvalidWorkflowException {
        BaseProcessorType processorNode = processorElement.getValue();
        RSProcessor<T> processor;

        if (processorNode instanceof SerialControllerType) {
            processor = createProcessor((SerialControllerType) processorNode);

        } else if (processorNode instanceof ParallelControllerType) {
            processor = createProcessor((ParallelControllerType) processorNode);

        } else if (processorNode instanceof ConditionalControllerType) {
            processor = createProcessor((ConditionalControllerType) processorNode);

        } else if (processorNode instanceof BusinessProcessorType){
            processor = createBusinessProcessor((BusinessProcessorType) processorNode);

        } else {
            throw new InvalidWorkflowDefinitionException(processorNode);
        }

        return processor;
    }

    /**
     *
     * @param <C>
     * @return
     * @throws InvalidWorkflowException
     */
    private <C extends RSProcessorContext> RSSerialController<C> createProcessor(SerialControllerType serialControllerType)
    throws InvalidWorkflowException {

        List<RSProcessor<C>> serialProcessorList = createProcessorList(serialControllerType.getProcessor());

        return createSerialController(serialProcessorList);
    }

    /**
     * This method is provided to support the case where a serial controller is implied rather than
     * explicitly named in the XML. For example:
     * <workflow:workflow xmlns:workflow="http://credit.paypal.com/v1/schema/workflow"
     *   context-class="com.paypal.credit.workflowtest.AccountIdProcessorContext">
     *     <workflow:process-list>
     *         <workflow:business-processor id="One" impl="com.paypal.credit.processors.ProcessorOne"/>
     *     </workflow:process-list>
     * </workflow:workflow>
     *
     * the above example should create a SerialController with one business processor.
     *
     * @param serialProcessorList
     * @param <C>
     * @return
     * @throws InvalidWorkflowException
     */
    private <C extends RSProcessorContext> RSSerialController<C> createSerialController(List<RSProcessor<C>> serialProcessorList)
    throws InvalidWorkflowException {

        RSSerialController<C> serialController = new RSSerialController<>(serialProcessorList);
        return serialController;
    }

    /**
     *
     * @param <T>
     * @param baseProcessorTypes
     * @return
     * @throws InvalidWorkflowException
     */
    private <T extends RSProcessorContext> List<RSProcessor<T>> createProcessorList(
            final List<JAXBElement<? extends BaseProcessorType>> baseProcessorTypes)
    throws InvalidWorkflowException {
        List<RSProcessor<T>> processorList = new ArrayList<>();
        for (JAXBElement<? extends BaseProcessorType> processorNode : baseProcessorTypes) {
            RSProcessor<T> processor = createProcessor(processorNode);
            if (processor != null) {
                processorList.add(processor);
            }
        }
        return processorList;
    }

    /**
     *
     * @param parallel
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private <T extends RSProcessorContext> RSParallelController<T> createProcessor(
            final ParallelControllerType parallel)
    throws InvalidWorkflowException {
        List<RSProcessor<T>> processorList = createProcessorList(parallel.getProcessor());

        RSParallelController<T> controller = new RSParallelController<>(processorList, getParallelExecutor());
        return controller;
    }

    /**
     *
     * @param conditional
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private <T extends RSProcessorContext> RSConditionalController<T> createProcessor(
            final ConditionalControllerType conditional)
    throws InvalidWorkflowException {
        RSProcessor<T> processor = createProcessor(conditional.getProcessor());

        RSConditionalController<T> controller =
                new RSConditionalController<>(conditional.getContextField(), conditional.getFieldValue(), processor);
        return controller;
    }

    /**
     *
     * @param processorNode
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private <T extends RSProcessorContext> RSProcessor<T> createBusinessProcessor(
            final BusinessProcessorType processorNode)
            throws InvalidWorkflowException {
        RSProcessor<T> processor = createBusinessProcessorInstanceFromName(processorNode.getImpl());

        return processor;
    }

    /**
     *
     * @param impl
     * @return
     * @throws InvalidWorkflowException
     */
    private RSProcessor createBusinessProcessorInstanceFromName(final String impl)
            throws InvalidWorkflowException {
        Class<?> processorClass = null;
        try {
            processorClass = Class.forName(impl);
        } catch (ClassNotFoundException e) {
            throw new UnknownWorkflowProcessorException(impl);
        }
        if (RSProcessor.class.isAssignableFrom(processorClass)) {
            try {
                RSProcessor<?> processor = (RSProcessor<?>) processorClass.newInstance();
                return processor;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new UnableToInstantiateWorkflowProcessorException(impl, e);
            }
        } else {
            throw new InvalidWorkflowProcessorException(impl);
        }

    }

}
