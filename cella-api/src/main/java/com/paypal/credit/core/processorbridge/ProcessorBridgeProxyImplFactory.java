package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.Application;
import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.commandprocessor.CommandProcessor;
import com.paypal.credit.core.commandprocessor.exceptions.FacadeProcessorBridgeNotAnInterfaceException;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeDefinesUnmappableMethodException;
import com.paypal.credit.core.commandprocessor.exceptions.ProcessorBridgeInstantiationException;
import com.paypal.credit.core.semantics.ProcessorBridgeMethodSemantics;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of the ProcessorBridgeFactory interface that
 * creates ProcessorBridge realizations as dynamic proxies.
 */
public class ProcessorBridgeProxyImplFactory
implements ProcessorBridgeFactory {

    /** A simple extends to make the typesafe Map that we need */
    private class ClassToInstanceCacheMap<T> extends ConcurrentHashMap<Class<T>, T> { }

    /** */
    private ClassToInstanceCacheMap processorBridgeCache = new ClassToInstanceCacheMap<>();

    private final Application application;

    public ProcessorBridgeProxyImplFactory(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    public ClassLoader getClassLoader() {
        return this.application.getClassLoader();
    }

    private CommandProcessor getCommandProcessor() {
        return this.application.getCommandProcessor();
    }

    /**
     * Create a new instance or return a mapped instance of a
     * ProcessorBridge interface.
     * @param processorBridgeClass the Class of the ProcessorBridge
     * @return A realization of the ProcessorBridge interface.
     */
    @Override
    public <T extends ProcessorBridge> T create(final Class<T> processorBridgeClass)
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
     *
     * @param processorBridgeClass
     * @param <T>
     * @return
     */
    protected <T extends ProcessorBridge> T createNewInstance(final Class<T> processorBridgeClass) {
        T result = (T)Proxy.newProxyInstance(
                getClassLoader(),
                new Class<?>[]{processorBridgeClass},
                new ProcessorBridgeProxyInvocationHandler(getApplication())
        );

        return result;
    }

    /**
     *
     * @param method
     * @param <RESULT>
     * @return
     */
    public <RESULT> Command<RESULT> createCommand(final Method method)
            throws ProcessorBridgeInstantiationException {
        CommandMapping commandMapping = method.getAnnotation(CommandMapping.class);

        if (commandMapping != null && commandMapping.commandClass() != null && commandMapping.commandClass().length() > 0) {
            return createCommand(method, commandMapping);
        } else {
            return createCommandFromMethodOnly(method);
        }
    }

    /**
     *
     * @param method
     * @param commandMapping
     * @param <R>
     * @return
     * @throws ProcessorBridgeDefinesUnmappableMethodException
     */
    private <R> Command<R> createCommand(final Method method, final CommandMapping commandMapping)
            throws ProcessorBridgeDefinesUnmappableMethodException {
        try {
            Class<R> commandClass = (Class<R>)Class.forName(commandMapping.commandClass());
            if (commandClass.isAssignableFrom(Command.class)) {

            } else {
                throw new ProcessorBridgeDefinesUnmappableMethodException(method);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param method
     * @param <R>
     * @return
     */
    private <R> Command<R> createCommandFromMethodOnly(final Method method) {
        return null;
    }

    /** */
    private <R, C extends Command<R>> R invokeCommand(C command) throws Exception {
        return getCommandProcessor().doSynchronously(command);
    }
}
