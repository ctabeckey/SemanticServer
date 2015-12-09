package com.paypal.credit.core;

import com.paypal.credit.core.commandprocessor.CommandProcessor;
import com.paypal.credit.core.commandprocessor.CommandProcessorDefaultImpl;
import com.paypal.credit.core.commandprovider.RootCommandProvider;
import com.paypal.credit.core.semantics.ApplicationSemantics;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.datasourceprovider.RootDataSourceProviderFactory;

/**
 * The container for all application components.
 *
 *
 */
public class Application {
    private final ClassLoader classLoader;
    private final ApplicationSemantics applicationSemantics;
    private final RootCommandProvider rootCommandProvider;
    private final RootDataSourceProviderFactory serviceProviderFactory;
    private final CommandProcessor commandProcessor;

    /**
     *
     * using the thread context class loader.
     * @param rootPackageName the name of the Package that contains all of the application
     *                        components. The structure under this package is expected to be:
     *                        - root.model, where the domain model resides
     * @return
     * @throws CoreRouterSemanticsException
     */
    public static Application create(final String rootPackageName)
            throws CoreRouterSemanticsException {
        return create(Thread.currentThread().getContextClassLoader(), rootPackageName);
    }

    /**
     *
     * @param classLoader - the class loader to use to load application components
     * @param rootPackageName the name of the Package that contains all of the application
     *                        components. The structure under this package is expected to be:
     *                        - root.model, where the domain model resides
     * @return
     * @throws CoreRouterSemanticsException
     */
    public static Application create(final ClassLoader classLoader, final String rootPackageName)
            throws CoreRouterSemanticsException {
        String modelPackage = rootPackageName == null ? "model" : rootPackageName + ".model";

        ApplicationSemantics semantics = new ApplicationSemantics(classLoader, modelPackage);
        RootCommandProvider commandProvider = new RootCommandProvider(classLoader);
        RootDataSourceProviderFactory serviceProviderFactory = RootDataSourceProviderFactory.getOrCreate(classLoader);
        CommandProcessor commandProcessor = CommandProcessorDefaultImpl.create();

        Application application = new Application(classLoader, semantics, commandProvider, serviceProviderFactory, commandProcessor);

        // required wiring between application components
        commandProcessor.setApplication(application);

        return application;
    }

    private Application(
            final ClassLoader classLoader,
            final ApplicationSemantics applicationSemantics,
            final RootCommandProvider rootCommandProvider,
            final RootDataSourceProviderFactory serviceProviderFactory,
            final CommandProcessor commandProcessor) {
        this.classLoader = classLoader;
        this.applicationSemantics = applicationSemantics;
        this.rootCommandProvider = rootCommandProvider;
        this.serviceProviderFactory = serviceProviderFactory;
        this.commandProcessor = commandProcessor;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ApplicationSemantics getApplicationSemantics() {
        return applicationSemantics;
    }

    public RootCommandProvider getRootCommandProvider() {
        return rootCommandProvider;
    }

    public RootDataSourceProviderFactory getServiceProviderFactory() {
        return serviceProviderFactory;
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public void shutdown() {
        getCommandProcessor().shutdown();
    }
    public boolean isShutdown() {
        return getCommandProcessor().isShutdown();
    }
}
