package org.nanocontext.semanticserver.test.processorbridge;

import org.nanocontext.semanticserver.test.model.Authorization;
import org.nanocontext.semanticserver.test.model.AuthorizationId;

/**
 * Created by cbeckey on 11/12/15.
 */
public interface SubjectProcessorBridge {
    /**  */
    AuthorizationId postAuthorization(
            final Authorization authorization
    );

    /** */
    Authorization getAuthorizationByAuthorizationId(
            final AuthorizationId authorizationId
    );
}
