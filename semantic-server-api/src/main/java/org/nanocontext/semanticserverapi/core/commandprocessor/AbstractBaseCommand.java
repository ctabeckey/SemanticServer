package org.nanocontext.semanticserverapi.core.commandprocessor;

import org.nanocontext.semanticserverapi.core.Application;

import java.util.concurrent.Callable;

/**
 * Created by cbeckey on 11/11/15.
 */
public abstract class AbstractBaseCommand<R>
implements Callable<R>, ApplicationAwareCommand {
    private Application application;
    private RoutingToken routingToken;

    /**
     * Provides environment access to the Command implementations.
     *
     * @param application
     */
    public void setApplicationContext(final Application application) {
        this.application = application;
    }

    protected Application getApplicationContext() {
        return application;
    }

    public RoutingToken getRoutingToken() {
        return routingToken;
    }
}
