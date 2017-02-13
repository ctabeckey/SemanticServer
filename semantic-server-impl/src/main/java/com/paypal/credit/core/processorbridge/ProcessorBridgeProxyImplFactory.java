package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;

import java.lang.reflect.Proxy;

/**
 * An implementation of the ProcessorBridgeFactory interface that
 * creates ProcessorBridge realizations as dynamic proxies.
 */
public class ProcessorBridgeProxyImplFactory
    extends AbstractCachingProcessorBridgeFactory
    implements ProcessorBridgeFactory {

    public ProcessorBridgeProxyImplFactory(Application application) {

        super(application);
    }

    /**
     *
     * @param processorBridgeClass
     * @param <T>
     * @return
     */
    protected <T> T createNewInstance(final Class<T> processorBridgeClass) {
        T result = (T)Proxy.newProxyInstance(
                getApplication().getClassLoader(),
                new Class<?>[]{processorBridgeClass},
                new ProcessorBridgeProxyInvocationHandler(getApplication())
        );

        return result;
    }
}
