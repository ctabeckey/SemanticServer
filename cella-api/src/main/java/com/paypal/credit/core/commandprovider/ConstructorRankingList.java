package com.paypal.credit.core.commandprovider;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.utility.ParameterCheckUtility;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * A List that maintains rankings of classes and constructor with respect to their "distance" from
 * a parameter type list.
 * Useful for finding the most applicable constructor for a parameter list.
 */
public class ConstructorRankingList {
    private final RoutingToken routingToken;
    private final String classSimpleName;
    private final Class<?>[] parameters;
    private final Class<?> resultType;
    private final List<CommandConstructorRank> candidates;

    public ConstructorRankingList(
            final RoutingToken routingToken,
            final String classSimpleName,
            final Class<?>[] parameters,
            final Class<?> resultType) {

        ParameterCheckUtility.checkParameterNotNull(routingToken, "routingToken");
        ParameterCheckUtility.checkParameterNotNull(classSimpleName, "classSimpleName");
        ParameterCheckUtility.checkParameterNotNull(parameters, "parameters");
        ParameterCheckUtility.checkParameterNotNull(resultType, "resultType");

        this.routingToken = routingToken;
        this.classSimpleName = classSimpleName;
        this.parameters = parameters;
        this.resultType = resultType;

        this.candidates = new ArrayList<>();
    }

    /**
     * The clazz must be the same as desiredClazz or a subclass of desiredClazz or this
     * method silently returns with no state change.
     * Constructors that cannot accept the parameters in the parameter list are silently
     * ignored.
     * @param clazz
     * @param ctor
     */
    public void add(final RoutingToken routingToken, final Class<? extends Command> clazz, final Constructor<?> ctor) {
        int distance = 0;
        if (this.classSimpleName.equals(clazz.getSimpleName())) {
            Class<?>[] ctorParameters = ctor.getParameterTypes();
            distance += calculateParameterDistance(parameters, ctorParameters);
            // if the ctor is applicable then add it to the list ordered by distance
            if (distance >= 0) {
                candidates.add(new CommandConstructorRank(routingToken, clazz, ctor, distance));
            }
        }
    }

    public int size() {
        return candidates.size();
    }

    public CommandConstructorRank get(final int index) {
        return candidates.get(index);
    }

    public void addAll(final ConstructorRankingList providerCommands) {
        for(CommandConstructorRank commandConstructor : providerCommands.candidates) {
            candidates.add(commandConstructor);
        }
    }

    /**
     * A simple object whose natural ordering
     * orders constructors by their applicability to a parameter list.
     */
    public static class CommandConstructorRank
            implements Comparable<CommandConstructorRank> {
        private final RoutingToken routingToken;
        private final Class<? extends Command> command;
        private final Constructor<?> ctor;
        private final int distance;

        public CommandConstructorRank(
                final RoutingToken routingToken,
                final Class<? extends Command> command,
                final Constructor<?> ctor,
                final int distance) {
            this.routingToken = routingToken;
            this.command = command;
            this.ctor = ctor;
            this.distance = distance;
        }

        public RoutingToken getRoutingToken() {
            return routingToken;
        }

        public Class<? extends Command> getCommand() {
            return command;
        }

        public Constructor<?> getCtor() {
            return ctor;
        }

        public int getDistance() {
            return distance;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         */
        @Override
        public int compareTo(final CommandConstructorRank o) {
            int result = this.getRoutingToken().compareTo(o.getRoutingToken());
            if (result == 0) {
                result = o.getDistance() - this.getDistance();
            }
            return result;
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
     * @param ctorA
     * @param ctorB
     * @return
     */
    public static int calculateParameterDistance(final Class<?>[] ctorA, final Class<?>[] ctorB) {
        if (ctorA.length != ctorB.length) {
            return Integer.MIN_VALUE;
        }
        int distance = 0;

        for (int index = 0; index < ctorA.length; ++ index) {
            Class<?> clazzA = ctorA[index];
            Class<?> clazzB = ctorB[index];

            if (clazzA.equals(clazzB)) {
                // does not increase distance
            } else if (clazzB.isAssignableFrom(clazzA)) {
                // if clazzB is either the same as, or a superclass of, clazzA
                distance++;
            } else {
                distance = Integer.MIN_VALUE;
            }
        }

        return distance;
    }
}
