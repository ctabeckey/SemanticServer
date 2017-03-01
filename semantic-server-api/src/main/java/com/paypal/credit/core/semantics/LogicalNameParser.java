package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

/**
 * Created by cbeckey on 2/24/17.
 */
public interface LogicalNameParser {
    ParsedName parse(String logicalName)
            throws CoreRouterSemanticsException;
}
