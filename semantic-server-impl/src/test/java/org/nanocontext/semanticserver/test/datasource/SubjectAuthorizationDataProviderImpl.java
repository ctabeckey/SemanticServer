package org.nanocontext.semanticserver.test.datasource;

import org.nanocontext.semanticserver.test.model.Authorization;
import org.nanocontext.semanticserver.test.model.AuthorizationId;

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
