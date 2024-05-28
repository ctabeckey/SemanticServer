package org.nanocontext.semanticserver.core.datasourceprovider;

import org.nanocontext.semanticserver.semanticserver.datasourceprovider.RootDataSourceProviderImpl;
import org.nanocontext.semanticserverapi.core.commandprocessor.RoutingToken;
import org.nanocontext.semanticserverapi.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import org.nanocontext.semanticserverapi.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;
import org.nanocontext.semanticserver.semanticserver.applicationbridge.ProductTypeRoutingToken;
import org.nanocontext.semanticserver.test.datasource.SubjectAuthorizationDataProvider;
import org.nanocontext.semanticserver.test.model.Authorization;
import org.nanocontext.semanticserver.test.model.AuthorizationId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 11/10/15.
 */
public class DataSourceProviderTest {
    @Test
    public void testFindDataSource() {
        RoutingToken rt = new ProductTypeRoutingToken("USAINS");
        RootDataSourceProviderImpl spf = RootDataSourceProviderImpl.getOrCreate();
        RootDataSourceProviderImpl.DataSourceDescription description =
                spf.findDataSource(SubjectAuthorizationDataProvider.class, rt);

        Assert.assertNotNull(description);
    }

    @Test
    public void testCreateDataSource()
            throws UnableToFindServiceProviderImplementation, UnableToCreateServiceProviderImplementation {
        RoutingToken rt = new ProductTypeRoutingToken("USAINS");
        RootDataSourceProviderImpl spf = RootDataSourceProviderImpl.getOrCreate();
        SubjectAuthorizationDataProvider dataSource = spf.createDataSource(SubjectAuthorizationDataProvider.class, rt);

        Assert.assertNotNull(dataSource);

        AuthorizationId result = dataSource.authorize(new Authorization());
        Assert.assertNotNull(result);
    }
}
