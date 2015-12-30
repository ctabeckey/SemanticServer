package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.workflow.RSProcessorContext;

/**
 * Created by cbeckey on 12/10/15.
 */
public interface ProcessorContextFactory {
    RSProcessorContext createContext(String contextClassName, Object[] parameters)             throws CommandInstantiationException;
}
