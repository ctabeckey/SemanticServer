package com.paypal.credit.core.serviceprovider.exceptions;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.serviceprovider.ServiceProviderInterface;

/**
 *
 */
public class UnableToFindServiceProviderImplementation extends Exception {
    /**
     * Create a useful debugging message
     *
     * @param dataSourceApi
     * @param routingToken
     * @return
     */
    private static String createMessage(final Class<? extends ServiceProviderInterface> dataSourceApi, final RoutingToken routingToken) {
        return String.format("Unable to find a data source provider for API(%s) to %s",
                dataSourceApi == null ? "<null>" : dataSourceApi.getName(),
                routingToken.toString());
    }

    /**
     *
     * @param dataSourceApi
     * @param routingToken
     * @param <S>
     */
    public <S extends ServiceProviderInterface> UnableToFindServiceProviderImplementation(
            final Class<S> dataSourceApi,
            final RoutingToken routingToken) {
        super(createMessage(dataSourceApi, routingToken));
    }

    /**
     *
     * @param dataSourceApi
     * @param routingToken
     * @param cause
     * @param <S>
     */
    public <S extends ServiceProviderInterface> UnableToFindServiceProviderImplementation(
            final Class<S> dataSourceApi,
            final RoutingToken routingToken,
            Throwable cause) {
        super(createMessage(dataSourceApi, routingToken), cause);
    }
}
