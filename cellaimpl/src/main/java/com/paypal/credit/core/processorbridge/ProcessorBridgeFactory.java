package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;

/**
 * More correctly called a ProcessorBridge Realization Factory, this interface defines
 * a factory that creates a realization of the ProcessorBridge.
 * A ProcessorBridge is an interface that defines the contract between a facade and an application.
 * A ProcessorBridge (interface) consists of semantically constrained methods that map to
 * available Application workflowcommand.
 */
public interface ProcessorBridgeFactory {
    /**
     *
     * @param <T>
     * @param facadeRouterClazz
     * @return
     */
    <T> T create(Class<T> facadeRouterClazz)
            throws ProcessorBridgeInstantiationException;
}
