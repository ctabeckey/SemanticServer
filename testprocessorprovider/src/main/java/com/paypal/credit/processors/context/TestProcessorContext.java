package com.paypal.credit.processors.context;

import com.paypal.credit.core.utility.ParameterCheckUtility;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.annotations.ProvidesGroups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *
 * The @ProvidesGroups annotation applies to the state of instances of this
 * class once they are constructed. In essence this means that Validation.validate()
 * invoked with the groups named in the ProvidesGroups is guaranteed to pass on a newly
 * constructed instance.
 * e.g.
 * TestProcessorContext testProcessorContextInstance = new TestProcessorContextInstance("");
 * Validator.validate(testProcessorContextInstance, {TestProcessorContext.AuthorizationIdValidationGroup.class});
 */
@ProvidesGroups({TestProcessorContext.AuthorizationIdValidationGroup.class})
public class TestProcessorContext extends RSProcessorContext {
    @NotNull(groups = {AuthorizationIdValidationGroup.class})
    private final String authorizationId;

    @NotNull(groups = {AccountIdValidationGroup.class})
    @Pattern(regexp = "[0-9]{13,16}", groups = {AuthorizationIdValidationGroup.class})
    private String accountId;

    /**
     *
     * @param authorizationId
     */
    public TestProcessorContext(String authorizationId) {
        ParameterCheckUtility.checkParameterNotNull(authorizationId, "authorizationId");
        this.put("authorizationId", authorizationId);
        this.authorizationId = authorizationId;
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    public String getAccountId() {
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
