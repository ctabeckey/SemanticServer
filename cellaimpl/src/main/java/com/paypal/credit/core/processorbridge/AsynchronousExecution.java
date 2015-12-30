package com.paypal.credit.core.processorbridge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that may be used on a method to indicate that the Command should be
 * executed asynchronously.
 * If the method parameters includes an implementation of CommandCallback as the first
 * parameter then that implementation will be called on completion, else no callback
 * is made and results, if any, are discarded.
 * The CommandCallback method parameter is NOT used when locating the implementing Command,
 * i.e. the Command should NOT include the CommandCallback as a constructor parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsynchronousExecution {
}
