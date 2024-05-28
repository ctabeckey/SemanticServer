package org.nanocontext.semanticserverapi.core.commandprocessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the command as eligible for asynchronous execution.
 *
 * NOTE: a method in the processor bridge interface must also be
 * annotated with AsynchronousExecution for it to be executed
 * asynchronously.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsynchronouslyExecutableCommand {
}
