package com.paypal.credit.core.applicationbridge;

import com.paypal.credit.core.Application;

import java.lang.reflect.Proxy;

/**
 * An implementation of the ProcessorBridgeFactory interface that
 * creates ProcessorBridge realizations as dynamic proxies.
 */
public class ApplicationBridgeProxyImplFactory
    extends AbstractCachingApplicationBridgeFactory
    implements ApplicationBridgeFactory {

    public ApplicationBridgeProxyImplFactory(Application application) {

        super(application);
    }

    /**
     *
     * @param applicationCapabilities
     * @param <T>
     * @return
     */
    protected <T> T createNewInstance(final Class<T> applicationCapabilities) {
        T result = (T)Proxy.newProxyInstance(
                getApplication().getClassLoader(),
                new Class<?>[]{applicationCapabilities},
                new ApplicationBridgeProxyInvocationHandler(getApplication())
        );

        return result;
    }
}
