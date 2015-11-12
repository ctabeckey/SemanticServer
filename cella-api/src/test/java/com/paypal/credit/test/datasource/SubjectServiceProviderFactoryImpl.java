package com.paypal.credit.test.datasource;

import com.paypal.credit.core.serviceprovider.RootServiceProviderFactory;
import com.paypal.credit.core.serviceprovider.ServiceProviderFactory;
import com.paypal.credit.test.ProductTypeRoutingToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cbeckey on 11/10/15.
 */
public class SubjectServiceProviderFactoryImpl
implements ServiceProviderFactory {

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
    public Set<RootServiceProviderFactory.DataSourceDescription<?>> getInstalledProviders() {
        Set<RootServiceProviderFactory.DataSourceDescription<?>> result =
                new HashSet<>();

        RootServiceProviderFactory.DataSourceDescription<SubjectAuthorizationDataProvider> dataProviderDataSourceDescription =
            new RootServiceProviderFactory.DataSourceDescription<SubjectAuthorizationDataProvider>(
                getPublisher(), 1, SubjectAuthorizationDataProvider.class, new ProductTypeRoutingToken("USAINS"),
                SubjectAuthorizationDataProviderImpl.class);

        result.add(dataProviderDataSourceDescription);
        return result;
    }
}
