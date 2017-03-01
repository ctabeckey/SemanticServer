package com.paypal.credit.core;

import com.paypal.credit.context.Context;
import com.paypal.credit.context.ContextFactory;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.core.commandprocessor.CommandProcessor;
import com.paypal.credit.core.commandprovider.CommandProvider;
import com.paypal.credit.core.datasourceprovider.DataSourceProvider;
import com.paypal.credit.core.datasourceprovider.RootDataSourceProvider;
import com.paypal.credit.core.semantics.ApplicationSemantics;
import com.paypal.credit.core.semantics.ApplicationSemanticsImpl;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * The container for all application components.
 * An Application is defined as a NanoContext that contains the following beans:
 * "root-class-loader" - a ClassLoader instance that will be used to load all of the other components
 * "semantics" - must implement ApplicationSemantics, provides the semantics of the application, including
 *               the model (the nouns)
 * "command-provider" - must implement CommandProvider, provides the application commands
 * "data-source-provider" - must implement RootDataSourceProvider, usually uses RootDataSourceProviderImpl and relies
 *                          on the Provider implementation of that that class
 * "command-processor" - must implement CommandProcessor, usually uses RootCommandProvider and relies on the
 *                       Provider implementation within that class
 */
public class ApplicationImpl implements Application {
    public final static String ROOT_CLASS_LOADER_BEAN = "root-class-loader";
    public final static String SEMANTICS_BEAN = "semantics";
    public final static String COMMAND_PROVIDER_BEAN = "command-provider";
    public final static String DATA_SOURCE_PROVIDER_BEAN = "data-source-provider";
    public final static String COMMAND_PROCESSOR_BEAN = "command-processor";

    /**
     *
     * @param includeDefaultBase
     * @param configurationResources
     * @return
     */
    public static Application create(final boolean includeDefaultBase, final String... configurationResources)
            throws FileNotFoundException, JAXBException, ContextInitializationException {
        Context context = null;
        if (includeDefaultBase) {
            InputStream defaultIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("baseapplication.xml");
            context = (new ContextFactory()).with(defaultIn).build();
        }

        if(configurationResources != null) {
            for (String configurationResource : configurationResources) {
                ContextFactory childContextFactory = new ContextFactory();

                InputStream configIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationResource);

                childContextFactory.with(configIn);
                if (context != null) {
                    childContextFactory.withParentContext(context);
                }
                context = childContextFactory.build();
            }
        }

        return new ApplicationImpl(context);
    }

    // ================================================================================================
    // Instance Members
    // ================================================================================================

    private final Context context;
    private final ClassLoader classLoader;
    private final ApplicationSemantics applicationSemantics;
    private final CommandProvider rootCommandProvider;
    private final RootDataSourceProvider serviceProvider;
    private final CommandProcessor commandProcessor;

    private ApplicationImpl(final Context context) throws ContextInitializationException {
        this.context = context;
        this.classLoader = context.getBean(ROOT_CLASS_LOADER_BEAN, ClassLoader.class);
        this.applicationSemantics = context.getBean(SEMANTICS_BEAN, ApplicationSemanticsImpl.class);
        this.rootCommandProvider = context.getBean(COMMAND_PROVIDER_BEAN, CommandProvider.class);
        this.serviceProvider = context.getBean(DATA_SOURCE_PROVIDER_BEAN, RootDataSourceProvider.class);
        this.commandProcessor = context.getBean(COMMAND_PROCESSOR_BEAN, CommandProcessor.class);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ApplicationSemantics getApplicationSemantics() {
        return applicationSemantics;
    }

    @Override
    public CommandProvider getRootCommandProvider() {
        return rootCommandProvider;
    }

    @Override
    public DataSourceProvider getServiceProvider() {
        return serviceProvider;
    }

    @Override
    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void shutdown() {
        getCommandProcessor().shutdown();
    }
    @Override
    public boolean isShutdown() {
        return getCommandProcessor().isShutdown();
    }
}
