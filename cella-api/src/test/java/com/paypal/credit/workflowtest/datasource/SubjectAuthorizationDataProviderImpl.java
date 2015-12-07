package com.paypal.credit.workflowtest.datasource;

import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;

/**
 * Created by cbeckey on 11/10/15.
 */
public class SubjectAuthorizationDataProviderImpl
implements SubjectAuthorizationDataProvider {
    /**
     * @param authorization
     */
    @Override
    public AuthorizationId authorize(final Authorization authorization) {
        return new AuthorizationId();
    }
}
