package org.nanocontext.semanticserverapi.core.semantics;

import java.util.concurrent.Callable;

/**
 * Created by cbeckey on 2/23/17.
 */
public interface CommandClassSemantics extends AbstractBaseSemantics {
    boolean describes(Class<? extends Callable<?>> commandClazz);

    String toBaseString();
}
