package com.paypal.credit.workflowcommand;

import org.nanocontext.semanticserverapi.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.workflow.RSProcessorContext;

/**
 * Created by cbeckey on 12/10/15.
 */
public interface ProcessorContextFactory {
    RSProcessorContext createContext(Class<?> contextClass, Object[] parameters)
            throws CommandInstantiationException;
}
