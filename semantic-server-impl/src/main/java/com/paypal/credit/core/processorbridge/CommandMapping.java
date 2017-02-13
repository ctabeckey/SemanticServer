package com.paypal.credit.core.processorbridge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that may be used to map a Method in a ProcessorBridge to a specific Command
 * type. By default the Command type is selected by naming convention.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandMapping {
    /** the Class of the Command */
    Class<?> value();
}
