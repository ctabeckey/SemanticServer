package com.paypal.credit.workflowcommand.datasource;

import com.paypal.credit.workflowcommand.model.Authorization;
import com.paypal.credit.workflowcommand.model.AuthorizationId;

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
        return new AuthorizationId("655321");
    }
}
