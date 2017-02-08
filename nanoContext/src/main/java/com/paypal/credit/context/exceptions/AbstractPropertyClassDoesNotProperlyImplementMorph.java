package com.paypal.credit.context.exceptions;

/**
 * Created by cbeckey on 3/17/16.
 */
public class AbstractPropertyClassDoesNotProperlyImplementMorph
        extends ContextInitializationException {
    private static final String createMessage(final Class<?> propertyClazz) {
        return String.format("%s does not implement morph method, default implementation only handles identical type.", propertyClazz == null ? "<null>" : propertyClazz.getName());
    }

    public AbstractPropertyClassDoesNotProperlyImplementMorph(final Class<?> propertyClazz) {
        super(createMessage(propertyClazz));
    }
}
