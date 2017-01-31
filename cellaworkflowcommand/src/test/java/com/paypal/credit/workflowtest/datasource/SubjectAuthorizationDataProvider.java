package com.paypal.credit.workflowtest.datasource;

import com.paypal.credit.workflowtest.model.Authorization;
import com.paypal.credit.workflowtest.model.AuthorizationId;

/**
 * Created by cbeckey on 11/10/15.
 */
public interface SubjectAuthorizationDataProvider {
    /** */
    AuthorizationId authorize(Authorization authorization);
}
