package org.nanocontext.semanticserver.test.commands;

import org.nanocontext.semanticserverapi.core.commandprocessor.AbstractBaseCommand;
import org.nanocontext.semanticserver.test.model.Authorization;
import org.nanocontext.semanticserver.test.model.AuthorizationId;

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
    public AuthorizationId call() {
        return new AuthorizationId();
    }

}
