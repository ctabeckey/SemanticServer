package org.nanocontext.semanticserverapi.core.commandprovider;

/**
 * A simple object definition that is passed from a CommandProvider to the RootCommandProvider
 * in response to a find() method call. The RootCommandProvider selects one of
 * the candidate commands and then calls the CommandProvider create method
 * with the instance of this class that the CommandProvider had created during the
 * find().
 * Other than the CommandProvider, which is used to select
 * the CommandProvider to call, the content of this class is treated as an opaque
 * token by the RootCommandProvider.
 */
public interface CommandInstantiationToken {
    /**
     * MUST be non-null
     * @return
     */
    CommandProvider getCommandProvider();
}
