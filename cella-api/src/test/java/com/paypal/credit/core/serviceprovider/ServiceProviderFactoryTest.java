package com.paypal.credit.core.serviceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.serviceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import com.paypal.credit.core.serviceprovider.exceptions.UnableToFindServiceProviderImplementation;
import com.paypal.credit.test.ProductTypeRoutingToken;
import com.paypal.credit.test.datasource.SubjectAuthorizationDataProvider;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/10/15.
 */
public class ServiceProviderFactoryTest {
    @Test
    public void testFindDataSource() {
        RoutingToken rt = new ProductTypeRoutingToken("USAINS");
        RootServiceProviderFactory spf = RootServiceProviderFactory.getOrCreate();
        RootServiceProviderFactory.DataSourceDescription description =
                spf.findDataSource(SubjectAuthorizationDataProvider.class, rt);

        Assert.assertNotNull(description);
    }

    @Test
    public void testCreateDataSource()
            throws UnableToFindServiceProviderImplementation, UnableToCreateServiceProviderImplementation {
        RoutingToken rt = new ProductTypeRoutingToken("USAINS");
        RootServiceProviderFactory spf = RootServiceProviderFactory.getOrCreate();
        SubjectAuthorizationDataProvider dataSource = spf.createDataSource(SubjectAuthorizationDataProvider.class, rt);

        Assert.assertNotNull(dataSource);

        AuthorizationId result = dataSource.authorize(new Authorization());
        Assert.assertNotNull(result);
    }
}
