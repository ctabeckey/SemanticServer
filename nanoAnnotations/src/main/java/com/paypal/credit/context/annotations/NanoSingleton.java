package com.paypal.credit.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker that a particular class must be managed as a Singleton
 * by NanoContext.
 * This annotation is a NanoBean and so may also include an identifier.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NanoSingleton {
    String identifier() default "";
}
