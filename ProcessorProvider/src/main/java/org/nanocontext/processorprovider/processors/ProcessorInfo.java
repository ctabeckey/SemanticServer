package com.paypal.credit.processors;

import com.paypal.credit.processors.exceptions.InvalidProcessorException;
import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflow.annotations.RemovesGroups;
import com.paypal.credit.workflow.annotations.RequiresGroups;

import javax.validation.constraints.NotNull;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public final class ProcessorInfo
extends SimpleBeanInfo
implements BeanInfo {
    /**
     *
     * @param processorClass
     * @param <P>
     * @param <R>
     * @return
     * @throws InvalidProcessorException
     */
    public static <P extends RSProcessor<R>, R extends RSProcessorContext> ProcessorInfo create(final Class<P> processorClass)
            throws InvalidProcessorException {
        // validate that the class is valid processor and return the process method therein
        Method processMethod = ProcessorUtilities.findProcessMethod(processorClass);
        // the context is the first and only parameter that a processor expects
        Class<R> contextClass = (Class<R>) processMethod.getParameterTypes()[0];

        RequiresGroups requires = processorClass.getAnnotation(RequiresGroups.class);
        ProvidesGroups provides = processorClass.getAnnotation(ProvidesGroups.class);
        RemovesGroups removes = processorClass.getAnnotation(RemovesGroups.class);

        return new ProcessorInfo(processorClass, contextClass, processMethod, requires, provides, removes);
    }

    // ===================================================================================================
    // Instance Members
    // ===================================================================================================

    private final Class<? extends RSProcessor> processorClass;
    private final Method processMethod;
    private final Class<? extends RSProcessorContext> contextClass;
    private final Set<Class<?>> requiresGroups;
    private final Set<Class<?>> providesGroups;
    private final Set<Class<?>> removesGroups;

    private final MethodDescriptor processMethodDescriptor;
    private final BeanDescriptor processorBeanDescriptor;

    /**
     * Create a completely unmodifiable ProcessorInfo instance.
     *
     * @param processorClass
     */
    private ProcessorInfo(
            final @NotNull Class<? extends RSProcessor> processorClass,
            final @NotNull Class<? extends RSProcessorContext> contextClass,
            final @NotNull Method processMethod,
            final RequiresGroups requires,
            final ProvidesGroups provides,
            final RemovesGroups removes
    ) {
        this.processorClass = processorClass;
        this.processMethod = processMethod;
        this.contextClass = contextClass;
        this.requiresGroups = requires == null || requires.value() == null || requires.value().length == 0 ? null :
                Collections.unmodifiableSet(new HashSet(Arrays.asList(requires.value())));
        this.providesGroups = provides == null || provides.value() == null || provides.value().length == 0 ? null :
                Collections.unmodifiableSet(new HashSet(Arrays.asList(provides.value())));
        this.removesGroups = removes == null || removes.value() == null || removes.value().length == 0 ? null :
                Collections.unmodifiableSet(new HashSet(Arrays.asList(removes.value())));

        // a process() method has a single parameter, which MUST be derived from RSProcessorContext
        //
        ParameterDescriptor processParameterDescriptor = new ParameterDescriptor();
        processParameterDescriptor.setDisplayName(this.contextClass.getSimpleName());
        processParameterDescriptor.setExpert(false);
        processParameterDescriptor.setHidden(false);
        processParameterDescriptor.setName(this.contextClass.getName());
        processParameterDescriptor.setShortDescription(
                ProcessorUtilities.createValidationGroupDescription("requiresGroups", requires == null ? null : requires.value())
                + ","
                + ProcessorUtilities.createValidationGroupDescription("providesGroups", provides == null ? null : provides.value())
                + ","
                + ProcessorUtilities.createValidationGroupDescription("removesGroups", removes == null ? null : removes.value())
        );
        this.processMethodDescriptor = new MethodDescriptor(this.processMethod, new ParameterDescriptor[]{processParameterDescriptor});
        this.processorBeanDescriptor = new BeanDescriptor(processorClass);
    }

    public final Class<? extends RSProcessor> getProcessorClass() {
        return processorClass;
    }

    public final Method getProcessMethod() {return processMethod;}

    public final Class<? extends RSProcessorContext> getContextClass() {
        return contextClass;
    }

    public final Set<Class<?>> getRequiresGroups() {
        return requiresGroups;
    }

    public final Set<Class<?>> getProvidesGroups() {
        return providesGroups;
    }

    public final Set<Class<?>> getRemovesGroups() {
        return removesGroups;
    }

    /**
     * Claim there are no other relevant BeanInfo objects.  You
     * may override this if you want to (for example) return a
     * BeanInfo for a base class.
     */
    @Override
    public final BeanInfo[] getAdditionalBeanInfo() {
        return super.getAdditionalBeanInfo();
    }

    /**
     * The process() method is the ONLY method that may be exposed.
     */
    @Override
    public final MethodDescriptor[] getMethodDescriptors() {

        return new MethodDescriptor[] {this.processMethodDescriptor};
    }

    /**
     * Provide a default BeanDescriptor
     */
    @Override
    public final BeanDescriptor getBeanDescriptor() {
        return this.processorBeanDescriptor;
    }

    /**
     * Provide an explicit PropertyDescriptor array, publishing the
     * "standard" properties of a Processor.
     */
    @Override
    public final PropertyDescriptor[] getPropertyDescriptors() {
        return super.getPropertyDescriptors();
    }

    // ==================================================================================================
    // Object method overrides
    // ==================================================================================================

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessorInfo that = (ProcessorInfo) o;
        return Objects.equals(getProcessorClass(), that.getProcessorClass()) &&
                Objects.equals(getProcessMethod(), that.getProcessMethod()) &&
                Objects.equals(getContextClass(), that.getContextClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProcessorClass(), getProcessMethod(), getContextClass());
    }

    @Override
    public String toString() {
        return "ProcessorInfo{" +
                "processorClass=" + processorClass +
                ", processMethod=" + processMethod +
                ", contextClass=" + contextClass +
                ", requiresGroups=" + requiresGroups +
                ", providesGroups=" + providesGroups +
                ", removesGroups=" + removesGroups +
                '}';
    }
}
