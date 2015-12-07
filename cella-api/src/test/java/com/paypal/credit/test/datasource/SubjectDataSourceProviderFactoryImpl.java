package com.paypal.credit.test.datasource;

import com.paypal.credit.core.datasourceprovider.RootDataSourceProviderFactory;
import com.paypal.credit.core.datasourceprovider.DataSourceProviderFactory;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbeckey on 11/10/15.
 */
public class SubjectDataSourceProviderFactoryImpl
implements DataSourceProviderFactory {

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
    public Set<RootDataSourceProviderFactory.DataSourceDescription<?>> getInstalledProviders() {
        Set<RootDataSourceProviderFactory.DataSourceDescription<?>> result =
                new HashSet<>();

        RootDataSourceProviderFactory.DataSourceDescription<SubjectAuthorizationDataProvider> dataProviderDataSourceDescription =
            new RootDataSourceProviderFactory.DataSourceDescription<SubjectAuthorizationDataProvider>(
                getPublisher(), 1, SubjectAuthorizationDataProvider.class, new ProductTypeRoutingToken("USAINS"),
                SubjectAuthorizationDataProviderImpl.class);

        result.add(dataProviderDataSourceDescription);
        return result;
    }
}
