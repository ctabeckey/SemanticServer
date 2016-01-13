package com.paypal.credit.processors.exceptions;

import com.paypal.credit.processors.ProcessorInfo;

/**
 * An exception thrown when the RootProcessorProvider is unable to find a
 * ProcessorProvider that can create the described Processor.
 */
public class UnknownProcessorDescriptorException extends ProcessorProviderException {
    private static String createMessage(final ProcessorInfo descriptor) {
        return String.format("Unable to create Processor %s, no provider is available", descriptor.toString());
    }

    public UnknownProcessorDescriptorException(final ProcessorInfo descriptor) {
        super(createMessage(descriptor));
    }
}
