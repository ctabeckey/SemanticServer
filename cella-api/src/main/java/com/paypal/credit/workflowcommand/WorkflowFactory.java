package com.paypal.credit.workflowcommand;

import com.paypal.credit.workflowcommand.exceptions.InvalidWorkflowDefinitionException;
import com.paypal.credit.workflowcommand.exceptions.InvalidWorkflowException;
import com.paypal.credit.workflowcommand.exceptions.InvalidWorkflowProcessorException;
import com.paypal.credit.workflowcommand.exceptions.UnableToInstantiateWorkflowProcessorException;
import com.paypal.credit.workflow.RSConditionalController;
import com.paypal.credit.workflow.RSParallelController;
import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.RSSerialController;
import com.paypal.credit.workflow.threadpool.RSThreadPoolExecutor;
import com.paypal.credit.workflowcommand.exceptions.UnknownWorkflowProcessorException;
import com.paypal.credit.workflowcommand.workflow.BaseProcessorType;
import com.paypal.credit.workflowcommand.workflow.ConditionalType;
import com.paypal.credit.workflowcommand.workflow.ParallelType;
import com.paypal.credit.workflowcommand.workflow.ProcessorType;
import com.paypal.credit.workflowcommand.workflow.SerialType;
import com.paypal.credit.workflowcommand.workflow.StartType;
import com.paypal.credit.workflowcommand.workflow.WorkflowType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbeckey on 11/13/15.
 */
public class WorkflowFactory {

    /**
     *
     * @param contextClass
     * @param workflowType
     * @param resultType
     * @param <C>
     * @return
     * @throws InvalidWorkflowException
     */
    public static <C extends RSProcessorContext> Workflow create(
            final Class<C> contextClass,
            final WorkflowType workflowType,
            final Class<?> resultType) throws InvalidWorkflowException {
        RSSerialController<C> controller = create(contextClass, workflowType);
        Workflow wf = new Workflow(controller, resultType);

        return wf;
    }

    /**
     *
     * @param contextClass
     * @param workflow
     * @param <C>
     * @return
     * @throws InvalidWorkflowException
     */
    public static <C extends RSProcessorContext> RSSerialController<C> create(
            final Class<C> contextClass,
            final WorkflowType workflow)
            throws InvalidWorkflowException {
        final RSThreadPoolExecutor parallelExecutor = RSThreadPoolExecutor.getBuilder().build();
        final StartType start = workflow.getStart();

        List<RSProcessor<C>> serialProcessorList = new ArrayList<>();
        for (Object processorNode : start.getParallelOrSerialOrConditional()) {
            RSProcessor<C> processor = createProcessor(processorNode, contextClass, parallelExecutor);
            if (processor != null) {
                serialProcessorList.add(processor);
            }
        }

        RSSerialController<C> serialController = new RSSerialController<>(serialProcessorList);
        return serialController;
    }

    /**
     *
     * @param processorNode
     * @param contextClass
     * @param parallelExecutor
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private static <T extends RSProcessorContext> RSProcessor<T> createProcessor(
            final Object processorNode,
            final Class<T> contextClass,
            final RSThreadPoolExecutor parallelExecutor)
            throws InvalidWorkflowException {
        RSProcessor<T> processor;
        if (processorNode instanceof SerialType) {
            processor = createProcessor((SerialType) processorNode, contextClass, parallelExecutor);
        } else if (processorNode instanceof ParallelType) {
            processor = createProcessor((ParallelType) processorNode, contextClass, parallelExecutor);
        } else if (processorNode instanceof ConditionalType) {
            processor = createProcessor((ConditionalType) processorNode, contextClass, parallelExecutor);
        } else if (processorNode instanceof ProcessorType){
            processor = createProcessor((ProcessorType) processorNode, contextClass, parallelExecutor);
        } else {
            throw new InvalidWorkflowDefinitionException(processorNode);
        }
        return processor;
    }

    /**
     *
     * @param baseProcessor
     * @param contextClass
     * @param parallelExecutor
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private static <T extends RSProcessorContext> List<RSProcessor<T>> createChildProcessorList(
            final BaseProcessorType baseProcessor,
            final Class<T> contextClass,
            final RSThreadPoolExecutor parallelExecutor)
            throws InvalidWorkflowException {
        List<RSProcessor<T>> processorList = new ArrayList<>();
        for (Object processorNode : baseProcessor.getParallelOrSerialOrConditional()) {
            RSProcessor<T> processor = createProcessor(processorNode, contextClass, parallelExecutor);
            if (processor != null) {
                processorList.add(processor);
            }
        }
        return processorList;
    }

    /**
     *
     * @param serial
     * @param contextClass
     * @param parallelExecutor
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private static <T extends RSProcessorContext> RSSerialController<T> createProcessor(
            final SerialType serial,
            final Class<T> contextClass,
            final RSThreadPoolExecutor parallelExecutor)
            throws InvalidWorkflowException {
        List<RSProcessor<T>> processorList = createChildProcessorList(serial, contextClass, parallelExecutor);

        RSSerialController<T> controller = new RSSerialController<>(processorList);
        return controller;
    }

    /**
     *
     * @param parallel
     * @param contextClass
     * @param parallelExecutor
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private static <T extends RSProcessorContext> RSParallelController<T> createProcessor(
            final ParallelType parallel,
            final Class<T> contextClass,
            final RSThreadPoolExecutor parallelExecutor)
            throws InvalidWorkflowException {
        List<RSProcessor<T>> processorList = createChildProcessorList(parallel, contextClass, parallelExecutor);

        RSParallelController<T> controller = new RSParallelController<>(processorList, parallelExecutor);
        return controller;
    }

    /**
     *
     * @param conditional
     * @param contextClass
     * @param parallelExecutor
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private static <T extends RSProcessorContext> RSConditionalController<T> createProcessor(
            final ConditionalType conditional,
            final Class<T> contextClass,
            final RSThreadPoolExecutor parallelExecutor)
            throws InvalidWorkflowException {
        RSProcessor<T> processor = createProcessor(conditional.getProcessor(), contextClass, parallelExecutor);

        RSConditionalController<T> controller =
                new RSConditionalController<>(conditional.getContextField(), conditional.getFieldValue(), processor);
        return controller;
    }

    /**
     *
     * @param processorNode
     * @param contextClass
     * @param parallelExecutor
     * @param <T>
     * @return
     * @throws InvalidWorkflowException
     */
    private static <T extends RSProcessorContext> RSProcessor<T> createProcessor(
            final ProcessorType processorNode,
            final Class<T> contextClass,
            final RSThreadPoolExecutor parallelExecutor)
            throws InvalidWorkflowException {
        RSProcessor<T> processor = createProcessor(processorNode.getImpl());

        return processor;
    }

    /**
     *
     * @param impl
     * @return
     * @throws InvalidWorkflowException
     */
    private static RSProcessor createProcessor(final String impl)
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
