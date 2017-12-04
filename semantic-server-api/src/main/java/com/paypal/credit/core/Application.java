package com.paypal.credit.core;

import com.paypal.credit.context.Context;
import com.paypal.credit.core.commandprocessor.CommandProcessor;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.datasourceprovider.DataSourceProvider;
import com.paypal.credit.core.datasourceprovider.RootDataSourceProvider;
import com.paypal.credit.core.semantics.ApplicationSemantics;

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
