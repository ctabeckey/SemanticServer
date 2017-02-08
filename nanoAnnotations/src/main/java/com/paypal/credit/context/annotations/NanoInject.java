package com.paypal.credit.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation on a constructor parameter that will cause the referenced
 * NanoContext managed bean to be injected as the constructor parameter.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface NanoInject {
    String identifier() default "";
}
