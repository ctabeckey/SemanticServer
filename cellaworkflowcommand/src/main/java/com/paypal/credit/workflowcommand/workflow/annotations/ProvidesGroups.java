package com.paypal.credit.workflowcommand.workflow.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Processor as providing an array of validation groups.
 * May also be used to annotate a ProcessorContext to denote that the
 * constructor guarantees that the groups will be available.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProvidesGroups {
    Class<?>[] value();
}
