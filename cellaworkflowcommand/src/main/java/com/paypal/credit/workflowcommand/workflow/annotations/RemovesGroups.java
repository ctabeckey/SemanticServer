package com.paypal.credit.workflowcommand.workflow.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation on a Processor that defines which validation groups the processor
 * may remove during its invocation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RemovesGroups {
    Class<?>[] value();
}
