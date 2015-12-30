package com.paypal.credit.workflowtest.model;

import javax.validation.constraints.NotNull;

/**
 * Created by cbeckey on 11/9/15.
 */
public class AccountId {
    private final String accountIdentifier;

    public AccountId(final @NotNull String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public String getAccountIdentifier() {
        return this.accountIdentifier;
    }
}
