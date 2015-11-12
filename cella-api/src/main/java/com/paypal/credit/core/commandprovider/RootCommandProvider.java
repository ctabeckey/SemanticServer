package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;

import java.lang.reflect.InvocationTargetException;
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
        this.classLoader = classLoader;
        this.commandProviderLoader = ServiceLoader.load(CommandProvider.class);
    }

    public ConstructorRankingList.CommandConstructorRank findCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Object[] parameters,
            final Class<?> resultType) {
        return findCommand(routingToken, commandClassSemantics, getTypes(parameters), resultType);
    }

    /**
     *
     */
    public ConstructorRankingList.CommandConstructorRank findCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameterTypes,
            final Class<?> resultType) {
        ConstructorRankingList commandRanking =
                findCommands(routingToken, commandClassSemantics, parameterTypes, resultType);

        return commandRanking.size() > 0 ? commandRanking.get(0) : null;
    }

    /**
     *
     */
    public <C extends Command> C createCommand(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Object[] parameters,
            final Class<?> resultType) {
        final Class<?>[] parameterTypes = getTypes(parameters);

        ConstructorRankingList.CommandConstructorRank commandConstructorRank =
                findCommand(routingToken, commandClassSemantics, parameterTypes, resultType);
        if (commandConstructorRank != null) {
            try {
                Object command = commandConstructorRank.getCtor().newInstance(parameters);
                return (C) command;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassCastException ccX) {
                ccX.printStackTrace();
            }
        }

        return null;
    }

    /**
     *
     * @param parameters
     * @return
     */
    private static Class<?>[] getTypes(final Object[] parameters) {
        final Class<?>[] parameterTypes = new Class<?>[parameters.length];

        for(int index = 0; index < parameters.length; ++index) {
            parameterTypes[index] = parameters[index].getClass();
        }
        return parameterTypes;
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
    public ConstructorRankingList findCommands(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {
        ConstructorRankingList commandRanking =
                new ConstructorRankingList(routingToken, commandClassSemantics.toString(), parameters, resultType);

        for (Iterator<CommandProvider> iter = this.commandProviderLoader.iterator(); iter.hasNext(); ) {
            CommandProvider commandProvider = iter.next();

            ConstructorRankingList providerCommands =
                    commandProvider.findCommands(routingToken, commandClassSemantics, parameters, resultType);
            if (providerCommands != null) {
                commandRanking.addAll(providerCommands);
            }
        }

        return commandRanking;
    }

    /**
     * Reload the service factories.
     */
    public final void reload() {
        this.commandProviderLoader.reload();
    }
}
