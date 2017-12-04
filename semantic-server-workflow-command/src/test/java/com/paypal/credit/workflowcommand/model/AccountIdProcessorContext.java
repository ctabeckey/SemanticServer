package com.paypal.credit.workflowcommand.model;

import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;

import javax.validation.constraints.NotNull;

/**
 *
 */
@ProvidesGroups({AccountIdProcessorContext.AuthorizationIdValidationGroup.class})
public class AccountIdProcessorContext extends RSProcessorContext {
    @NotNull(groups = {AuthorizationIdValidationGroup.class})
    private final AuthorizationId authorizationId;

    @NotNull(groups = {AccountIdValidationGroup.class})
    private AccountId accountId;

    /**
     *
     * @param authorizationId
     */
    public AccountIdProcessorContext(AuthorizationId authorizationId) {
        this.put("authorizationId", authorizationId);
        this.authorizationId = authorizationId;
    }

    public AuthorizationId getAuthorizationId() {
        return authorizationId;
    }

    public AccountId getAccountId() {
        return this.accountId;
    }

    // ==============================================================================
    // A separate validation group is declared on each field.
    // The validation for each field must include the group for the
    // processor chaining validation to work correctly
    // ==============================================================================
    public static class AuthorizationIdValidationGroup{}
    public static class AccountIdValidationGroup{}
}
