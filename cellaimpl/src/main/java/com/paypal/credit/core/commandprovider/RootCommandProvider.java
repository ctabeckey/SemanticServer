package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.exceptions.CommandProviderException;
import com.paypal.credit.core.commandprovider.exceptions.MultipleCommandImplementationsException;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.utility.ParameterCheckUtility;
import com.paypal.credit.utility.TypeAndInstanceUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class RootCommandProvider
implements CommandProvider {

    // ============================================================================
    // Static Methods to manage instances of this class
    // ============================================================================

    /** A cache of class instances mapped to the ClassLoader that they load from */
    private static final Map<ClassLoader, RootCommandProvider> rootCommandProviderCache =
            new ConcurrentHashMap<>();

    /**
     *
     * @return
     */
    public static RootCommandProvider getOrCreate() {
        return getOrCreate(Thread.currentThread().getContextClassLoader());
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public static RootCommandProvider getOrCreate(ClassLoader classLoader) {
        RootCommandProvider commandProvider = null;
        commandProvider = rootCommandProviderCache.get(classLoader);
        if (commandProvider == null) {
            // Note that there is a small chance that a root command provider will get created
            // multiple times. The first copy will get dropped after the second copy
            // is inserted in the Map
            commandProvider = new RootCommandProvider(classLoader);
            rootCommandProviderCache.put(classLoader, commandProvider);
        }

        return commandProvider;
    }

    // ============================================================================
    // RootCommandProvider implementation
    // ============================================================================

    /** The ONLY ServiceLoader for the RootCommandProvider type */
    private final ServiceLoader<CommandProvider> commandProviderLoader;

    /** */
    private final ClassLoader classLoader;

    /**
     */
    public RootCommandProvider(final ClassLoader classLoader) {
        ParameterCheckUtility.checkParameterNotNull(classLoader, "classLoader");

        this.classLoader = classLoader;
        this.commandProviderLoader = ServiceLoader.load(CommandProvider.class, this.classLoader);
    }

    /**
     * This (RootCommandProvider) is the ONLY CommandProvider that can and must return null
     * as the publisher.
     */
    @Override
    public String getPublisher() {
        return null;
    }

    /**
     *
     */
    public <R, C extends Callable<R>> C createCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Object[] parameters,
            final Class<R> resultType)
            throws CommandProviderException {
        final Class<?>[] parameterTypes = TypeAndInstanceUtility.getTypes(parameters);

        CommandInstantiationToken commandInstantiationToken =
                findCommand(routingToken, commandClassSemantics, parameterTypes, resultType);
        if (commandInstantiationToken != null) {
            return (C) createCommand(commandInstantiationToken, parameters);
        }

        return null;
    }

    private boolean isKnownProvider(final CommandProvider commandProvider) {
        for (Iterator<CommandProvider> iter = this.commandProviderLoader.iterator(); iter.hasNext(); ) {
            // reference equality is intentional, this MUST be the instance that this root manages
            if (commandProvider == iter.next()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reload the service factories.
     */
    public final void reload() {
        this.commandProviderLoader.reload();
    }

    // ============================================================================
    // CommandProvider implementation
    // ============================================================================

    /**
     *
     * @param routingToken
     * @param commandClassSemantics
     * @param parameterTypes
     * @param resultType
     * @return
     * @throws CommandProviderException
     */
    @Override
    public CommandInstantiationToken findCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameterTypes,
            final Class<?> resultType)
            throws CommandProviderException {
        ParameterCheckUtility.checkParameterNotNull(routingToken, "routingToken");
        ParameterCheckUtility.checkParameterNotNull(commandClassSemantics, "commandClassSemantics");

        List<CommandInstantiationToken> commandInstantiationTokens = new ArrayList<>();

        for (Iterator<CommandProvider> iter = this.commandProviderLoader.iterator(); iter.hasNext(); ) {
            CommandProvider commandProvider = iter.next();

            CommandInstantiationToken providerToken =
                    commandProvider.findCommand(routingToken, commandClassSemantics, parameterTypes, resultType);
            if (providerToken != null) {
                commandInstantiationTokens.add(providerToken);
            }
        }
        if (commandInstantiationTokens.size() == 1) {
            return commandInstantiationTokens.get(0);
        } else if (commandInstantiationTokens.size() == 0) {
            return null;
        } else {
            throw new MultipleCommandImplementationsException(commandClassSemantics, commandInstantiationTokens);
        }
    }

    /**
     *
     * @param commandInstantiationToken
     * @param parameters
     * @return
     * @throws CommandProviderException
     */
    @Override
    public Callable<?> createCommand(
            final CommandInstantiationToken commandInstantiationToken,
            final Object[] parameters)
            throws CommandProviderException {
        ParameterCheckUtility.checkParameterNotNull(commandInstantiationToken, "commandInstantiationToken");

        // Security check
        if (isKnownProvider(commandInstantiationToken.getCommandProvider())) {
            return commandInstantiationToken.getCommandProvider().createCommand(commandInstantiationToken, parameters);
        }

        return null;
    }
}
