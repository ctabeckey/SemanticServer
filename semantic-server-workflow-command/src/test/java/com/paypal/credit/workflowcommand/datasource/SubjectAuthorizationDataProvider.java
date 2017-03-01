package com.paypal.credit.workflowcommand.datasource;

import com.paypal.credit.workflowcommand.model.Authorization;
import com.paypal.credit.workflowcommand.model.AuthorizationId;

/**
 * Created by cbeckey on 11/10/15.
 */
public interface SubjectAuthorizationDataProvider {
    /** */
    AuthorizationId authorize(Authorization authorization);
}
