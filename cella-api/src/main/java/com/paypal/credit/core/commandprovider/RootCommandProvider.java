package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.core.commandprovider.exceptions.InvalidTokenException;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.core.utility.TypeAndInstanceUtility;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
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

    public CommandInstantiationToken findMostApplicableCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Object[] parameters,
            final Class<?> resultType) {
        return findMostApplicableCommand(
                routingToken,
                commandClassSemantics,
                TypeAndInstanceUtility.getTypes(parameters),
                resultType);
    }

    /**
     *
     */
    public CommandInstantiationToken findMostApplicableCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameterTypes,
            final Class<?> resultType) {
        CommandLocationTokenRankedSet commandRanking =
                findCommands(routingToken, commandClassSemantics, parameterTypes, resultType);

        return commandRanking.size() > 0 ? commandRanking.first() : null;
    }

    /**
     *
     */
    public <C extends Command> C createCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Object[] parameters,
            final Class<?> resultType)
            throws CommandInstantiationException, InvalidTokenException {
        final Class<?>[] parameterTypes = TypeAndInstanceUtility.getTypes(parameters);

        CommandInstantiationToken commandInstantiationToken =
                findMostApplicableCommand(routingToken, commandClassSemantics, parameterTypes, resultType);
        if (commandInstantiationToken != null) {
            return (C) createCommand(routingToken, commandInstantiationToken, parameters);
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
     * @param parameters
     * @return
     */
    @Override
    public CommandLocationTokenRankedSet findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {
        ParameterCheckUtility.checkParameterNotNull(routingToken, "routingToken");
        ParameterCheckUtility.checkParameterNotNull(commandClassSemantics, "commandClassSemantics");

        CommandLocationTokenRankedSet commandRanking =
                new CommandLocationTokenRankedSet(routingToken, commandClassSemantics, parameters, resultType);

        for (Iterator<CommandProvider> iter = this.commandProviderLoader.iterator(); iter.hasNext(); ) {
            CommandProvider commandProvider = iter.next();

            CommandLocationTokenRankedSet providerCommands =
                    commandProvider.findCommands(routingToken, commandClassSemantics, parameters, resultType);
            if (providerCommands != null) {
                commandRanking.addAll(providerCommands);
            }
        }

        return commandRanking;
    }

    @Override
    public Command<?> createCommand(
            final RoutingToken routingToken,
            final CommandInstantiationToken commandInstantiationToken,
            final Object[] parameters)
            throws CommandInstantiationException, InvalidTokenException {
        ParameterCheckUtility.checkParameterNotNull(routingToken, "routingToken");

        if (isKnownProvider(commandInstantiationToken.getCommandProvider())) {
            return commandInstantiationToken.getCommandProvider().createCommand(routingToken, commandInstantiationToken, parameters);
        }

        return null;
    }
}
