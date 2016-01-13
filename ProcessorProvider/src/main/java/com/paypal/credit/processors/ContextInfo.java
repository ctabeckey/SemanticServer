package com.paypal.credit.processors;

import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;

import javax.validation.constraints.NotNull;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Bean Information about ProcessorContext derived classes.
 */
public class ContextInfo
extends SimpleBeanInfo
implements BeanInfo {

    /**
     *
     * @param contextClass
     * @return
     */
    public static ContextInfo create(Class<? extends RSProcessorContext> contextClass) {
        ProvidesGroups provides = contextClass.getAnnotation(ProvidesGroups.class);
        return new ContextInfo(contextClass, provides);
    }

    // ===================================================================================================
    // Instance Members
    // ===================================================================================================

    private final Class<? extends RSProcessorContext> contextClass;
    private final Set<Class<?>> providesGroups;
    private BeanDescriptor contextBeanDescriptor;

    /**
     * Create a completely unmodifiable ContextInfo instance.
     *
     * @param contextClass
     */
    public ContextInfo(final @NotNull Class<? extends RSProcessorContext> contextClass, ProvidesGroups provides) {
        this.contextClass = contextClass;
        this.providesGroups = provides == null || provides.value() == null || provides.value().length == 0 ? null :
                Collections.unmodifiableSet(new HashSet(Arrays.asList(provides.value())));

        // a process() method has a single parameter, which MUST be derived from RSProcessorContext
        //
        ParameterDescriptor processParameterDescriptor = new ParameterDescriptor();
        processParameterDescriptor.setDisplayName(this.contextClass.getSimpleName());
        processParameterDescriptor.setExpert(false);
        processParameterDescriptor.setHidden(false);
        processParameterDescriptor.setName(this.contextClass.getName());
        processParameterDescriptor.setShortDescription(
                ProcessorUtilities.createValidationGroupDescription("constructorProvidesGroups", provides.value())
        );
    }

    public Class<? extends RSProcessorContext> getContextClass() {
        return contextClass;
    }

    public Set<Class<?>> getProvidesGroups() {
        return providesGroups;
    }

    /**
     * The process() method is the ONLY method that may be exposed.
     */
    @Override
    public MethodDescriptor[] getMethodDescriptors() {

        return new MethodDescriptor[] {};
    }

    /**
     * Provide a default BeanDescriptor
     */
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return this.contextBeanDescriptor;
    }

    // ==================================================================================================
    // Object method overrides
    // ==================================================================================================


    @Override
    public String toString() {
        return "ContextInfo{" +
                "contextClass=" + contextClass +
                ", providesGroups=" + providesGroups +
                ", contextBeanDescriptor=" + contextBeanDescriptor +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextInfo that = (ContextInfo) o;
        return Objects.equals(getContextClass(), that.getContextClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContextClass());
    }
}
