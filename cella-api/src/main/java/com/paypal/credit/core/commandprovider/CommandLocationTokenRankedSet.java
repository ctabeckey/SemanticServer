package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.semantics.CommandClassSemantics;
import com.paypal.credit.core.utility.ParameterCheckUtility;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A List that maintains rankings of classes and constructor with respect to their "distance" from
 * a parameter type list.
 * Useful for finding the most applicable constructor for a parameter list.
 */
public class CommandLocationTokenRankedSet {
    private final RoutingToken routingToken;
    private final CommandClassSemantics commandClassSemantics;
    private final Class<?>[] parameters;
    private final Class<?> resultType;
    private final SortedSet<CommandInstantiationToken> candidates;

    public CommandLocationTokenRankedSet(
            final RoutingToken routingToken,
            final CommandClassSemantics commandClassSemantics,
            final Class<?>[] parameters,
            final Class<?> resultType) {

        ParameterCheckUtility.checkParameterNotNull(routingToken, "routingToken");
        ParameterCheckUtility.checkParameterNotNull(commandClassSemantics, "commandClassSemantics");
        ParameterCheckUtility.checkParameterNotNull(parameters, "parameters");
        ParameterCheckUtility.checkParameterNotNull(resultType, "resultType");

        this.routingToken = routingToken;
        this.commandClassSemantics = commandClassSemantics;
        this.parameters = parameters;
        this.resultType = resultType;

        this.candidates = new TreeSet<CommandInstantiationToken>(new DistanceComparator());
    }

    /**
     * The clazz must have the same simple name or this
     * method silently returns with no state change.
     * Constructors that cannot accept the parameters in the parameter list are silently
     * ignored.
     * @param clazz
     * @param ctor
     */
    public void add(
            final CommandProvider commandProvider,
            final RoutingToken routingToken,
            final Class<? extends Command> clazz,
            final Constructor<?> ctor) {
        ParameterCheckUtility.checkParameterNotNull(commandProvider, "commandProvider");
        ParameterCheckUtility.checkParameterNotNull(routingToken, "routingToken");
        ParameterCheckUtility.checkParameterNotNull(clazz, "clazz");
        ParameterCheckUtility.checkParameterNotNull(ctor, "ctor");

        if (getCommandClassSimpleName().equals(clazz.getSimpleName())) {
            // if the ctor is applicable then add it to the list ordered by distance
            add(new CommandInstantiationTokenImpl(commandProvider, clazz, ctor));
        }
    }

    /**
     *
     * @param commandProvider
     * @param commandFactory
     */
    public void add(
            final CommandProvider commandProvider,
            final CommandFactory commandFactory) {
        ParameterCheckUtility.checkParameterNotNull(commandProvider, "commandProvider");
        ParameterCheckUtility.checkParameterNotNull(commandFactory, "commandFactory");

        add(new CommandInstantiationTokenImpl(commandProvider, commandFactory));
    }

    /**
     *
     * @param token
     */
    public void add(CommandInstantiationTokenImpl token) {
        ParameterCheckUtility.checkParameterNotNull(token, "token");

        candidates.add(token);
    }

    /**
     *
     * @param providerCommands
     */
    public void addAll(final CommandLocationTokenRankedSet providerCommands) {
        for(CommandInstantiationToken commandConstructor : providerCommands.candidates) {
            candidates.add(commandConstructor);
        }
    }

    /**
     *
     * @return
     */
    public int size() {
        return candidates.size();
    }

    public CommandInstantiationToken first() {
        return this.candidates.first();
    }

    public Iterator<CommandInstantiationToken> iterator() {
        return this.candidates.iterator();
    }

    public CommandInstantiationToken last() {
        return this.candidates.last();
    }

    public RoutingToken getRoutingToken() {
        return routingToken;
    }

    public CommandClassSemantics getCommandClassSemantics() {
        return commandClassSemantics;
    }

    public String getCommandClassSimpleName() {
        return commandClassSemantics.toString();
    }

    public Class<?>[] getParameters() {
        return parameters;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    /**
     *
     */
    private class DistanceComparator implements Comparator<CommandInstantiationToken> {
        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         */
        @Override
        public int compare(final CommandInstantiationToken a, final CommandInstantiationToken b) {
            if (a instanceof CommandInstantiationTokenImpl
                    && b instanceof CommandInstantiationTokenImpl) {
                CommandInstantiationTokenImpl ctorTokenA = (CommandInstantiationTokenImpl)a;
                CommandInstantiationTokenImpl ctorTokenB = (CommandInstantiationTokenImpl)b;

                if (ctorTokenA.getFactory() != null && ctorTokenB.getFactory() != null) {
                    return 0;
                } else if (ctorTokenA.getFactory() != null && ctorTokenB.getFactory() == null) {
                    return -1;
                } else if (ctorTokenA.getFactory() == null && ctorTokenB.getFactory() != null) {
                    return 1;
                } else {
                    int distanceA = calculateParameterDistance(parameters, ctorTokenA.getCtor().getParameterTypes());
                    int distanceB = calculateParameterDistance(parameters, ctorTokenB.getCtor().getParameterTypes());

                    return distanceB - distanceA;
                }
            } else {
                return 0;
            }
        }

        /**
         * Determine the "distance" from the parameter list ctorA to the parameter list ctorB.
         * If the result is 0 then the parameter lists are identical.
         * If the result is positive then ctorB represents a parameter list that can accept the
         * parameter types in ctorA. The greater the value the "further" the parameter lists
         * are apart.
         * If the result is negative then ctorB represents a parameter list that cannot accept
         * the types represented by ctorB
         *
         * @param parametersA
         * @param parametersB
         * @return
         */
        private int calculateParameterDistance(final Class<?>[] parametersA, final Class<?>[] parametersB) {
            if (parametersA.length != parametersB.length) {
                return Integer.MIN_VALUE;
            }
            int distance = 0;

            for (int index = 0; index < parametersA.length; ++ index) {
                Class<?> clazzA = parametersA[index];
                Class<?> clazzB = parametersB[index];

                if (clazzA.equals(clazzB)) {
                    // does not increase distance
                } else if (clazzB.isAssignableFrom(clazzA)) {
                    // if clazzB is either the same as, or a superclass of, clazzA
                    distance++;
                } else {
                    distance = Integer.MIN_VALUE;
                    break;
                }
            }

            return distance;
        }
    }
}
