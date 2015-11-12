package com.paypal.credit.core.serviceprovider;

import java.util.Set;

/**
 * Created by cbeckey on 11/10/15.
 */
public interface ServiceProviderFactory {

    String getPublisher();

    Set<RootServiceProviderFactory.DataSourceDescription<?>> getInstalledProviders();
}
