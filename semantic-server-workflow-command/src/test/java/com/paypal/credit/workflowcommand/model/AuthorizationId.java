package com.paypal.credit.workflowcommand.model;

import javax.validation.constraints.NotNull;

/**
 * Created by cbeckey on 11/9/15.
 */
public class AuthorizationId {
    private final String authorizationIdentifier;

    public AuthorizationId(final @NotNull String authorizationIdentifier) {
        this.authorizationIdentifier = authorizationIdentifier;
    }

    public String getAuthorizationIdentifier() {
        return this.authorizationIdentifier;
    }
}
