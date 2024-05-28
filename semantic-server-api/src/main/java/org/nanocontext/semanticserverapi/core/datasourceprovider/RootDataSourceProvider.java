package org.nanocontext.semanticserverapi.core.datasourceprovider;

import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;
import org.nanocontext.semanticserverapi.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import org.nanocontext.semanticserverapi.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;

/**
 * The interface made available to the application components from the data source provider
 * hierarchy.
 */
public interface RootDataSourceProvider extends DataSourceProvider{
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
