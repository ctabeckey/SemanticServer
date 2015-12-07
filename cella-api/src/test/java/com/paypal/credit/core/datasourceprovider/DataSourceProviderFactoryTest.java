package com.paypal.credit.core.datasourceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;
import com.paypal.credit.core.processorbridge.ProductTypeRoutingToken;
import com.paypal.credit.test.datasource.SubjectAuthorizationDataProvider;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/10/15.
 */
public class DataSourceProviderFactoryTest {
    @Test
    public void testFindDataSource() {
        RoutingToken rt = new ProductTypeRoutingToken("USAINS");
        RootDataSourceProviderFactory spf = RootDataSourceProviderFactory.getOrCreate();
        RootDataSourceProviderFactory.DataSourceDescription description =
                spf.findDataSource(SubjectAuthorizationDataProvider.class, rt);

        Assert.assertNotNull(description);
    }

    @Test
    public void testCreateDataSource()
            throws UnableToFindServiceProviderImplementation, UnableToCreateServiceProviderImplementation {
        RoutingToken rt = new ProductTypeRoutingToken("USAINS");
        RootDataSourceProviderFactory spf = RootDataSourceProviderFactory.getOrCreate();
        SubjectAuthorizationDataProvider dataSource = spf.createDataSource(SubjectAuthorizationDataProvider.class, rt);

        Assert.assertNotNull(dataSource);

        AuthorizationId result = dataSource.authorize(new Authorization());
        Assert.assertNotNull(result);
    }
}
