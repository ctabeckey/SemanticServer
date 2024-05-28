package com.paypal.credit.processors;

import com.paypal.credit.ReflectionUtilities;
import com.paypal.credit.processors.exceptions.NoApplicableConstructorException;
import com.paypal.credit.processors.exceptions.ProcessorInstantiationException;
import com.paypal.credit.processors.exceptions.ProcessorProviderException;
import com.paypal.credit.processors.exceptions.UnknownProcessorDescriptorException;
import com.paypal.credit.workflow.RSProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by cbeckey on 1/8/16.
 */
public abstract class ProcessorProviderImpl
implements com.paypal.credit.processors.ProcessorProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorProviderImpl.class);

    /**
     * Return a Set of all available ProcessorDescriptor. Each represents
     * a single available Processor. This is the only method that must be implemented
     * by derived types.
     *
     * @return
     */
    @Override
    public abstract Set<ProcessorInfo> getProcessorInfos()
            throws ProcessorProviderException;

    /**
     * @param descriptor
     * @return
     */
    @Override
    public boolean canCreateProcessor(final ProcessorInfo descriptor)
            throws ProcessorProviderException {
        for (ProcessorInfo pi : getProcessorInfos()) {
            if (pi.equals(descriptor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Create a Processor instance from the Descriptor and the configuration
     * values.
     *
     * @param descriptor        a ProcessorDescriptor that uniquely identifies the Processor to create
     * @param settings an extension of BaseSettings that provides access to the
     *                          configuration parameters. The CommandProvider SHOULD use
     *                          the bean naming convention to match configuration getters
     *                          to processor setters.
     * @return an instance of the identified Processor
     */
    @Override
    public RSProcessor createProcessor(final @NotNull ProcessorInfo descriptor, final @NotNull Object settings)
            throws ProcessorProviderException {
        if (!canCreateProcessor(descriptor)) {
            throw new UnknownProcessorDescriptorException(descriptor);
        }

        Object processor = null;
        try {
            processor = ReflectionUtilities.createInstanceFromSettings(descriptor.getProcessorClass(), settings);
        } catch (NoApplicableConstructorException | IntrospectionException | InstantiationException | IllegalAccessException | InvocationTargetException nacX) {
            throw new ProcessorInstantiationException(descriptor, nacX);
        }

        try {
            return (RSProcessor) processor;
        } catch(ClassCastException ccX) {
            throw new ProcessorInstantiationException(descriptor, ccX);
        }
    }
}
