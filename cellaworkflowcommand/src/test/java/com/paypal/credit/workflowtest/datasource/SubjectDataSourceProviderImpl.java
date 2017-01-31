package com.paypal.credit.workflowtest.datasource;

import com.paypal.credit.core.datasourceprovider.DataSourceProvider;
import com.paypal.credit.core.datasourceprovider.RootDataSourceProvider;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbeckey on 11/10/15.
 */
public class SubjectDataSourceProviderImpl
implements DataSourceProvider {

    /**
     * The publisher of the service providers.
     *
     * @return an identifier of the publisher, usable only as a human readable String
     */
    @Override
    public String getPublisher() {
        return "TestProvider";
    }

    @Override
    public Set<RootDataSourceProvider.DataSourceDescription<?>> getInstalledProviders() {
        Set<RootDataSourceProvider.DataSourceDescription<?>> result =
                new HashSet<>();

        RootDataSourceProvider.DataSourceDescription<SubjectAuthorizationDataProvider> dataProviderDataSourceDescription =
            new RootDataSourceProvider.DataSourceDescription<SubjectAuthorizationDataProvider>(
                getPublisher(), 1, SubjectAuthorizationDataProvider.class, new ProductTypeRoutingToken("USAINS"),
                SubjectAuthorizationDataProviderImpl.class);

        result.add(dataProviderDataSourceDescription);
        return result;
    }
}
