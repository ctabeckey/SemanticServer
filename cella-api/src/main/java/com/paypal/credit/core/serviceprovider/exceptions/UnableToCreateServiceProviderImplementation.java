package com.paypal.credit.core.serviceprovider.exceptions;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.serviceprovider.ServiceProviderInterface;

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
            final Class<? extends ServiceProviderInterface> dataSourceApi,
            final RoutingToken routingToken,
            final Class<? extends ServiceProviderInterface> implementingClass) {
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
    public <S extends ServiceProviderInterface> UnableToCreateServiceProviderImplementation(
            final Class<S> dataSourceApi,
            final RoutingToken routingToken,
            final Class<? extends ServiceProviderInterface> implementingClass) {
        super(createMessage(dataSourceApi, routingToken, implementingClass));
    }

    /**
     *
     * @param dataSourceApi
     * @param routingToken
     * @param cause
     * @param <S>
     */
    public <S extends ServiceProviderInterface> UnableToCreateServiceProviderImplementation(
            final Class<S> dataSourceApi,
            final RoutingToken routingToken,
            final Class<? extends ServiceProviderInterface> implementingClass,
            Throwable cause) {
        super(createMessage(dataSourceApi, routingToken, implementingClass), cause);
    }
}
