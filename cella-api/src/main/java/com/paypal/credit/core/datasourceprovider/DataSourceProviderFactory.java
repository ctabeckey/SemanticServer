package com.paypal.credit.core.datasourceprovider;

import java.util.Set;

/**
 * Created by cbeckey on 11/10/15.
 */
public interface DataSourceProviderFactory {

    String getPublisher();

    Set<RootDataSourceProviderFactory.DataSourceDescription<?>> getInstalledProviders();
}
