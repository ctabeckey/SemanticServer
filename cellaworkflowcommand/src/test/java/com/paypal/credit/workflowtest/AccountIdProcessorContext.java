package com.paypal.credit.workflowtest;

import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflowcommand.workflow.annotations.ProvidesGroups;
import com.paypal.credit.workflowtest.model.AccountId;
import com.paypal.credit.workflowtest.model.AuthorizationId;

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
        ParameterCheckUtility.checkParameterNotNull(authorizationId, "authorizationId");
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
