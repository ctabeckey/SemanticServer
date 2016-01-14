package com.paypal.credit.workflow;

import com.paypal.credit.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RemovesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;
import com.paypal.credit.workflow.exceptions.RSWorkflowException;
import com.paypal.credit.workflow.exceptions.CompositeWorkflowContextException;
import com.paypal.credit.workflow.exceptions.DeprivedWorkflowContextException;
import com.paypal.credit.workflow.exceptions.ProcessorProvidesAndRemovesException;
import com.paypal.credit.workflow.exceptions.WorkflowContextException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a workflow, a connected graph of RSProcessor instances.
 * Provides validation of the provided, removed and required processor
 * context validation groups.
 */
public class Workflow<C extends RSProcessorContext, R>
extends RSSerialController<C> {
    // ===========================================================================
    // Instance Members
    // ===========================================================================

    private final Class<C> contextClass;

    /**
     *
     * @param processors
     * @param contextClass
     */
    public Workflow(final List<RSProcessor<C>> processors, final Class<C> contextClass) {
        super(processors);
        ParameterCheckUtility.checkParameterNotNull(contextClass, "contextClass");

        this.contextClass = contextClass;
    }

    /**
     * Validate that the Workflow is internally consistent:
     * 1.) that the initial conditions are met by the initial conditions of the context
     * 2.) that the sum of context state changes provide each processor with its required
     *     validation groups
     */
    public void validate() throws WorkflowContextException {
        AvailableValidationGroups availableValidationGroups = new AvailableValidationGroups();
        availableValidationGroups.modifyByClassAnnotations(contextClass);

        validateSerialProcessors(getProcessors(), availableValidationGroups);
    }

    /**
     * Validate an RSSerialProcessor derived class.
     *
     * @param serialController
     * @param availableValidationGroups
     * @throws WorkflowContextException
     */
    public void validate(
            final RSSerialController<C> serialController,
            final AvailableValidationGroups availableValidationGroups)
    throws WorkflowContextException {
        validateSerialProcessors(serialController.getProcessors(), availableValidationGroups);
    }

    /**
     * Validate an RSParallelProcessor derived class.
     *
     * @param parallelController
     * @param availableValidationGroups
     * @throws WorkflowContextException
     */
    public void validate(
            final RSParallelController<C> parallelController,
            final AvailableValidationGroups availableValidationGroups)
    throws WorkflowContextException {
        validateParallelProcessors(parallelController.getProcessors(), availableValidationGroups);
    }

    private void validateSerialProcessors(
            final List<RSProcessor<C>> processors,
            final AvailableValidationGroups availableValidationGroups)
    throws WorkflowContextException {
        CompositeWorkflowContextException exceptions = new CompositeWorkflowContextException();

        for (RSProcessor<C> processor : processors) {
            try {
                validate(processor, availableValidationGroups);
            } catch (WorkflowContextException x) {
                exceptions.add(x);
            }
        }

        if (exceptions.hasExceptions()) {
            throw exceptions;
        }
    }

    private void validateParallelProcessors(
            final List<RSProcessor<C>> processors,
            final AvailableValidationGroups availableValidationGroups)
    throws WorkflowContextException {
        CompositeWorkflowContextException exceptions = new CompositeWorkflowContextException();

        AvailableValidationGroups processorIntersectionValidationGroups = null;
        for (RSProcessor<C> processor : processors) {
            // make a copy here because the parallel processors may diverge
            // with respect to validation groups
            AvailableValidationGroups processorValidationGroups = availableValidationGroups.clone();

            try {
                validate(processor, processorValidationGroups);
            } catch (WorkflowContextException x) {
                exceptions.add(x);
            }

            // take the intersection of the processor validation groups,
            // which will be the available validation groups when this method exits.
            if (processorIntersectionValidationGroups == null) {
                processorIntersectionValidationGroups = processorValidationGroups;
            } else {
                processorIntersectionValidationGroups.intersect(processorValidationGroups);
            }
        }

        availableValidationGroups.replaceAll(processorIntersectionValidationGroups);

        if (exceptions.hasExceptions()) {
            throw exceptions;
        }
    }

    public void validate(
            final RSProcessor<C> processor,
            final AvailableValidationGroups processorValidationGroups)
    throws WorkflowContextException {
        CompositeWorkflowContextException exceptions = new CompositeWorkflowContextException();

        Class<?> processorClass = processor.getClass();
        try {
            processorValidationGroups.includesRequired(processorClass);
        } catch (WorkflowContextException x) {
            exceptions.add(x);
        }

        if (RSSerialController.class.isAssignableFrom(processorClass)) {
            validate((RSSerialController<C>)processor, processorValidationGroups);
        }

        if (RSParallelController.class.isAssignableFrom(processorClass)) {
            validate((RSParallelController<C>)processor, processorValidationGroups);
        }

        try {
            processorValidationGroups.modifyByClassAnnotations(processorClass);
        } catch (WorkflowContextException x) {
            exceptions.add(x);
        }

        if (exceptions.hasExceptions()) {
            throw exceptions;
        }
    }

    /**
     * Execute the workflow with the given parameters
     *
     * @return
     * @throws RSWorkflowException
     */
    public boolean execute(C context) throws RSWorkflowException {
        // create a Serial Controller to start from a single point
        RSSerialController<C> serialController = new RSSerialController<>(this.getProcessors());
        return serialController.process(context);
    }

    private class AvailableValidationGroups
            extends HashSet<Class<?>>
    implements Cloneable {

        public AvailableValidationGroups() {

        }

        private AvailableValidationGroups(AvailableValidationGroups that) {
            this.addAll(that);
        }

        @Override
        public synchronized AvailableValidationGroups clone() {
            return new AvailableValidationGroups(this);
        }

        public boolean modifyByClassAnnotations(Class<?> clazz)
                throws ProcessorProvidesAndRemovesException {
            ProvidesGroups contextProvides = clazz.getAnnotation(ProvidesGroups.class);
            RemovesGroups contextRemoves = clazz.getAnnotation(RemovesGroups.class);

            // if both provides and removes are provided then there can be no overlap in membership
            if (contextProvides != null && contextProvides.value() != null && contextProvides.value().length > 0
                    && contextRemoves != null && contextRemoves.value() != null && contextRemoves.value().length > 0 ) {
                for (Class<?> removed : contextRemoves.value()) {
                    if (Arrays.binarySearch(contextProvides.value(), removed) >= 0) {
                        throw new ProcessorProvidesAndRemovesException(clazz, removed);
                    }
                }
            }

            return
                addAll(contextProvides != null ? contextProvides.value() : null) ||
                removeAll(contextRemoves != null ? contextRemoves.value() : null);
        }

        /**
         *
         * @param providedValidationClasses
         * @return
         */
        public boolean addAll(Class<?>[] providedValidationClasses) {
            boolean result = false;
            if (providedValidationClasses != null) {
                for (Class<?> providedValidationClass : providedValidationClasses) {
                    if (providedValidationClass != null) {
                        result |= this.add(providedValidationClass);
                    }
                }
            }
            return result;
        }

        public boolean removeAll(Class<?>[] removedValidationClasses) {
            boolean result = false;
            if (removedValidationClasses != null) {
                for (Class<?> removedValidationClass : removedValidationClasses) {
                    if (removedValidationClass != null) {
                        result |= this.remove(removedValidationClass);
                    }
                }
            }
            return result;
        }

        public void includesRequired(Class<?> clazz) throws WorkflowContextException {
            RequiresGroups contextRequires = clazz.getAnnotation(RequiresGroups.class);

            if (contextRequires != null) {
                includesAll(clazz, contextRequires.value());
            };
        }

        public void includesAll(Class<?> processorClass, Class<?>[] requiredValidationClasses) throws WorkflowContextException {
            CompositeWorkflowContextException exceptions = new CompositeWorkflowContextException();

            if (requiredValidationClasses != null) {
                for (Class<?> requiredValidationClass : requiredValidationClasses) {
                    if (requiredValidationClass != null) {
                        if (! this.contains(requiredValidationClass)) {
                            exceptions.add(new DeprivedWorkflowContextException(processorClass, requiredValidationClass));
                        }
                    }
                }
            }

            if (exceptions.hasExceptions()) {
                throw exceptions;
            }
        }

        /**
         *
         * @param processorValidationGroups
         */
        public void intersect(final AvailableValidationGroups processorValidationGroups) {
            ParameterCheckUtility.checkParameterNotNull(processorValidationGroups, "processorValidationGroups");

            List<Class<?>> deadMenWalking = new ArrayList<>();
            for (Class<?> availableGroup : this) {
                if (! processorValidationGroups.contains(availableGroup)) {
                    deadMenWalking.add(availableGroup);
                }
            }

            this.removeAll(deadMenWalking);
        }

        /**
         *
         * @param processorIntersectionValidationGroups
         */
        public void replaceAll(final AvailableValidationGroups processorIntersectionValidationGroups) {
            this.clear();
            this.addAll(processorIntersectionValidationGroups);
        }
    }
}
