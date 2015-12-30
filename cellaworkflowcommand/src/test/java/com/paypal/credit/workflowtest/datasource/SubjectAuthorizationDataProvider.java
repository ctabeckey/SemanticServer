package com.paypal.credit.workflowtest.datasource;

import com.paypal.credit.core.datasourceprovider.DataSourceProviderInterface;
import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;

/**
 * Created by cbeckey on 11/10/15.
 */
public interface SubjectAuthorizationDataProvider
extends DataSourceProviderInterface {
    /** */
    AuthorizationId authorize(Authorization authorization);
}
