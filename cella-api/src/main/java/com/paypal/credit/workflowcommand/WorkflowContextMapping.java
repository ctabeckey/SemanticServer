package com.paypal.credit.workflowcommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides information to the workflow command creation to map the
 * parameters of a facade method into the processor context.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface WorkflowContextMapping {
    String value();
}
