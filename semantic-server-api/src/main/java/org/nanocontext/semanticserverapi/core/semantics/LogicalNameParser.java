package org.nanocontext.semanticserverapi.core.semantics;

import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;

/**
 * Created by cbeckey on 2/24/17.
 */
public interface LogicalNameParser {
    ParsedName parse(String logicalName)
            throws CoreRouterSemanticsException;
}
