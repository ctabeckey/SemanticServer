package com.paypal.credit.processors;

import com.paypal.credit.processors.exceptions.ProcessorProviderException;
import com.paypal.credit.settings.BaseSettings;
import com.paypal.credit.workflow.RSProcessor;

import java.util.Set;

/**
 * THe definition of a CommandProvider
 */
public interface ProcessorProvider {
    /**
     * The publisher is strictly for informational purposes.
     * @return
     */
    String getPublisher();

    /**
     * Return a Set of all available ProcessorDescriptor. Each represents
     * a single available Processor.
     *
     * @return
     */
    Set<ProcessorInfo> getProcessorInfos()
    throws ProcessorProviderException;

    /**
     *
     * @param descriptor
     * @return
     */
    boolean canCreateProcessor(ProcessorInfo descriptor) throws ProcessorProviderException;

    /**
     * Create a Processor instance from the Descriptor and the configuration
     * values.
     *
     * @param descriptor a ProcessorDescriptor that uniquely identifies the Processor to create
     * @param processorSettings a Java Bean containing the processor configuration
     *                          parameters. The CommandProvider SHOULD use
     *                          the bean naming convention to match configuration getters
     *                          to processor setters.
     * @return an instance of the identified Processor
     */
    RSProcessor createProcessor(ProcessorInfo descriptor, Object processorSettings)
    throws ProcessorProviderException;

}
