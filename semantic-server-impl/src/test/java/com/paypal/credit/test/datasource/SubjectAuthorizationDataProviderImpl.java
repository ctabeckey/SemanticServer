package com.paypal.credit.test.datasource;

import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;

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
