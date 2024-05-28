package org.nanocontext.semanticserverapi.core;

import com.paypal.credit.context.Context;
import org.nanocontext.semanticserverapi.core.commandprocessor.CommandProcessor;
import org.nanocontext.semanticserverapi.core.commandprovider.CommandProvider;
import org.nanocontext.semanticserverapi.core.datasourceprovider.RootDataSourceProvider;
import org.nanocontext.semanticserverapi.core.semantics.ApplicationSemantics;

/**
 * Created by cbeckey on 2/24/17.
 */
public interface Application {
    ClassLoader getClassLoader();

    ApplicationSemantics getApplicationSemantics();

    CommandProvider getRootCommandProvider();

    RootDataSourceProvider getServiceProvider();

    CommandProcessor getCommandProcessor();

    Context getContext();

    void shutdown();

    boolean isShutdown();
}
