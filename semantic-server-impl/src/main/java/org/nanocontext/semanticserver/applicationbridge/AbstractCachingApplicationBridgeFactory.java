package org.nanocontext.semanticserver.semanticserver.applicationbridge;

import org.nanocontext.semanticserverapi.core.Application;
import org.nanocontext.semanticserver.semanticserver.commandprocessor.exceptions.FacadeProcessorBridgeNotAnInterfaceException;
import org.nanocontext.semanticserver.semanticserver.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * An abstract implementation of the ProcessorBridgeFactory interface that
 * provides caching for derived classes.
 */
public abstract class AbstractCachingApplicationBridgeFactory
implements ApplicationBridgeFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractCachingApplicationBridgeFactory.class);

    private final Application application;

    /** A simple extends to make the typesafe Map that we need */
    private class ClassToInstanceCacheMap<T> extends ConcurrentHashMap<Class<T>, T> { }

    /** A cache of interface definitions mapped to (generated) class realizations */
    private ClassToInstanceCacheMap processorBridgeCache = new ClassToInstanceCacheMap<>();

    public AbstractCachingApplicationBridgeFactory(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    /**
     * Create a new instance or return a mapped instance of a
     * ProcessorBridge interface.
     * @param processorBridgeClass the Class of the ProcessorBridge
     * @return A realization of the ProcessorBridge interface.
     */
    @Override
    public <T> T create(final Class<T> processorBridgeClass)
    throws ProcessorBridgeInstantiationException {
        if (! processorBridgeClass.isInterface()) {
            throw new FacadeProcessorBridgeNotAnInterfaceException(processorBridgeClass);
        }

        T result = (T) processorBridgeCache.get(processorBridgeClass);

        if (result == null) {
            result = createNewInstance(processorBridgeClass);
            processorBridgeCache.put(processorBridgeClass, result);
        }

        return result;
    }

    /**
     * Derived class must implement the "real" processor bridge creation.
     *
     * @param processorBridgeClass the interface to create from
     * @param <T> the interface type
     * @return a realization of the interface
     */
    protected abstract <T> T createNewInstance(final Class<T> processorBridgeClass)
            throws ProcessorBridgeInstantiationException;
}
