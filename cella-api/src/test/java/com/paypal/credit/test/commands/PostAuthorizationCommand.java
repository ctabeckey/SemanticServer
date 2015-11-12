package com.paypal.credit.test.commands;

import com.paypal.credit.core.commandprocessor.AbstractBaseCommand;
import com.paypal.credit.test.model.Authorization;
import com.paypal.credit.test.model.AuthorizationId;

/**
 * Created by cbeckey on 11/11/15.
 */
public class PostAuthorizationCommand
        extends AbstractBaseCommand<AuthorizationId> {
    private final Authorization authorization;

    /**
     *
     * @param authorization
     */
    public PostAuthorizationCommand(Authorization authorization) {
        this.authorization = authorization;
    }

    /**
     * A synchronous execution of this command.
     * Asynchronous execution is managed by the commandprocessor, this is the
     * (only) invocation of the command.
     *
     * @return
     */
    @Override
    public AuthorizationId invoke() {
        return new AuthorizationId();
    }

}
