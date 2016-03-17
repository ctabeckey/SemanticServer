package com.paypal.credit.context.exceptions;

import com.paypal.credit.context.AbstractProperty;

/**
 * An exception that is thrown when a morph method is asked to
 * do a conversion that it cannot do.
 */
public class InvalidMorphTargetException extends ContextInitializationException {
    private final static String createMessage(final AbstractProperty property, final Class<?> baseType, final Class<?> targetType) {
        return String.format("Property %s, unable to morph from %s to %s", property.getClass(), baseType.getName(), targetType.getName());
    }

    /**
     * The only contsructor
     * @param property the property instance that was asked to morph()
     * @param baseType the base type of the property
     * @param targetType the target type of the property
     */
    public InvalidMorphTargetException(final AbstractProperty property, final Class<?> baseType, final Class<?> targetType) {
        super(createMessage(property, baseType, targetType));
    }
}
