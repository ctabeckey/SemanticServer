package com.paypal.credit.core.commandprocessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cbeckey on 11/12/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandExecution {
    /** mark the command as asynchronously executable **/
    boolean asynchronouslyExecutable() default false;
}
