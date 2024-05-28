package com.paypal.credit.processors.exceptions;

import com.paypal.credit.settings.BaseSettings;

/**
 * Created by cbeckey on 1/12/16.
 */
public class NoApplicableConstructorException extends Exception {
    private static String createMessage(final Class<?> processorClass, final Object settings) {
        return String.format("Unable to find constructor in %s for settings %s",
                processorClass == null ? "<null>" : processorClass.getName(),
                settings.toString()
        );
    }

    /**
     *
     * @param processorClass
     * @param settings
     */
    public NoApplicableConstructorException(final Class<?> processorClass, final Object settings) {
        super(createMessage(processorClass, settings));
    }
}
