package com.paypal.credit.core.datasourceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;

/**
 * The interface made available to the application components from the data source provider
 * hierarchy.
 */
public interface RootDataSourceProvider {
    /**
     *
     * @param dataSourceApi
     * @param routingToken
     * @param dataSourceProviderExceptionHandlers
     * @param <S>
     * @return
     */
    <S> S createDataSource(
            Class<S> dataSourceApi,
            RoutingToken routingToken,
            DataSourceProviderExceptionHandler... dataSourceProviderExceptionHandlers)
            throws UnableToFindServiceProviderImplementation, UnableToCreateServiceProviderImplementation;

}
