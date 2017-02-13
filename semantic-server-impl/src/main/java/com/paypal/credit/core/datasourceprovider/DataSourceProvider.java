package com.paypal.credit.core.datasourceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;

import java.util.Set;

/**
 * The interface provided by Data Source Providers to make the data sources available to
 * the application framework.
 */
public interface DataSourceProvider {
    /**
     *
     * @return
     */
    String getPublisher();

    /**
     *
     * @return
     */
    Set<RootDataSourceProviderImpl.DataSourceDescription<?>> getInstalledProviders();
}