package com.paypal.credit.test.datasource;

import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;

/**
 * Created by cbeckey on 11/10/15.
 */
public interface SubjectAuthorizationDataProvider {
    /** */
    AuthorizationId authorize(Authorization authorization);
}
