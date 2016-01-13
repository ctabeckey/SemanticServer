package com.paypal.credit.processors.exceptions;

import com.paypal.credit.processors.ProcessorInfo;

/**
 * An exception thrown when the RootProcessorProvider is unable to find a
 * ProcessorProvider that can create the described Processor.
 */
public class ProcessorInstantiationException extends ProcessorProviderException {
    private static String createMessage(final ProcessorInfo descriptor, Throwable cause) {
        return String.format(
                "Unable to create Processor %s, instantiation failed with %s",
                descriptor.toString(),
                cause.getClass().getSimpleName()
        );
    }

    public ProcessorInstantiationException(final ProcessorInfo descriptor, Throwable cause) {
        super(createMessage(descriptor, cause));
    }
}
