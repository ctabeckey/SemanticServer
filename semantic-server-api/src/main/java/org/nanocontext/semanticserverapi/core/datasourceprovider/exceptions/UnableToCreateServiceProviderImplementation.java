package org.nanocontext.semanticserverapi.core.datasourceprovider.exceptions;

import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;

/**
 *
 */
public class UnableToCreateServiceProviderImplementation extends Exception {
    /**
     * Create a useful debugging message
     *
     * @param dataSourceApi
     * @param routingToken
     * @return
     */
    private static String createMessage(
            final Class<?> dataSourceApi,
            final RoutingToken routingToken,
            final Class<?> implementingClass) {
        return String.format("Unable to create an instance of data source provider for API(%s) to %s, of class %s",
                dataSourceApi == null ? "<null>" : dataSourceApi.getName(),
                routingToken.toString(),
                implementingClass == null ? "<null>" : implementingClass.getName());
    }

    /**
     *
     * @param dataSourceApi
     * @param routingToken
     * @param <S>
     */
    public <S> UnableToCreateServiceProviderImplementation(
            final Class<S> dataSourceApi,
            final RoutingToken routingToken,
            final Class<?> implementingClass) {
        super(createMessage(dataSourceApi, routingToken, implementingClass));
    }

    /**
     *
     * @param dataSourceApi
     * @param routingToken
     * @param cause
     * @param <S>
     */
    public <S> UnableToCreateServiceProviderImplementation(
            final Class<S> dataSourceApi,
            final RoutingToken routingToken,
            final Class<?> implementingClass,
            Throwable cause) {
        super(createMessage(dataSourceApi, routingToken, implementingClass), cause);
    }
}
