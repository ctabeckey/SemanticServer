package com.paypal.credit.processors;

import com.paypal.credit.processors.exceptions.ProcessorProviderException;
import com.paypal.credit.processors.exceptions.UnknownProcessorDescriptorException;
import com.paypal.credit.settings.BaseSettings;
import com.paypal.credit.workflow.RSProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class RootProcessorProvider
implements ProcessorProvider {
    /** The class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RootProcessorProvider.class);

    // ============================================================================
    // Static Methods to manage instances of this class
    // ============================================================================

    /** A cache of class instances mapped to the ClassLoader that they load from */
    private static final Map<ClassLoader, RootProcessorProvider> rootCommandProviderCache =
            new ConcurrentHashMap<>();

    /**
     *
     * @return
     */
    public static RootProcessorProvider getOrCreate() {
        return getOrCreate(Thread.currentThread().getContextClassLoader());
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public static RootProcessorProvider getOrCreate(ClassLoader classLoader) {
        RootProcessorProvider commandProvider = null;
        commandProvider = rootCommandProviderCache.get(classLoader);
        if (commandProvider == null) {
            // Note that there is a small chance that a root command provider will get created
            // multiple times. The first copy will get dropped after the second copy
            // is inserted in the Map
            commandProvider = new RootProcessorProvider(classLoader);
            rootCommandProviderCache.put(classLoader, commandProvider);
        }

        return commandProvider;
    }

    // ============================================================================
    // RootProcessorProvider implementation
    // ============================================================================
    private ReentrantReadWriteLock processorDescriptorLock = new ReentrantReadWriteLock();
    private Set<ProcessorInfo> processorInfos = new HashSet<>();

    /** The ONLY ServiceLoader for the RootCommandProvider type */
    private final ServiceLoader<ProcessorProvider> providerLoader;

    /** */
    private final ClassLoader classLoader;

    /**
     */
    private RootProcessorProvider(final ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.providerLoader = ServiceLoader.load(ProcessorProvider.class, this.classLoader);
    }

    /**
     * This (RootCommandProvider) is the ONLY CommandProvider that can and must return null
     * as the publisher.
     */
    public String getPublisher() {
        return null;
    }

    /**
     * Reload the service factories.
     */
    public final void reload() throws ProcessorProviderException {
        this.providerLoader.reload();
        invalidateCaches();
    }

    private void invalidateCaches() throws ProcessorProviderException {
        getProcessorDescriptors(true);
    }

    // ============================================================================
    // ProcessorProvider implementation
    // ============================================================================

    /**
     * Find ProcessorInfo instances that match the given patterns of
     * processor class name and context class name.
     *
     * @param processorRegex a REGEX pattern for matching processor class names
     * @param useSimpleProcessorNames whether to use the FQ or the simple class names
     * @param contextClass the context class to restrict the results to, the resulting
     *                     Set will contain processors whose context is either the given
     *                     class or isAssignable from the given class. In other words the
     *                     RSProcessor that will accept the given type.
     *
     * @return a Set of ProcessorInfo meeting the given criteria
     * @throws ProcessorProviderException
     */
    public Set<ProcessorInfo> findProcessorInfos(
            final String processorRegex, final boolean useSimpleProcessorNames,
            final Class<?> contextClass)
            throws ProcessorProviderException {
        Set<ProcessorInfo> result = new HashSet<ProcessorInfo>();

        Pattern processPattern = processorRegex == null ? null : Pattern.compile(processorRegex);

        for (ProcessorInfo processor : getProcessorDescriptors(false)) {
            String processorClassName = useSimpleProcessorNames ? processor.getProcessorClass().getSimpleName() : processor.getProcessorClass().getName();

            if ((processPattern == null || processPattern.matcher(processorClassName).matches())
                    && (contextClass == null || processor.getContextClass().isAssignableFrom(contextClass))) {
                result.add(processor);
            }
        }
        return result;
    }

    /**
     * Return a Set of all available ProcessorDescriptor. Each represents
     * a single available Processor.
     *
     * @return
     */
    public Set<ProcessorInfo> getProcessorInfos() throws ProcessorProviderException {
        return getProcessorDescriptors(false);
    }

    /**
     * A lock is used to protect against simultaneous read and write of the
     * cached processDescriptors. Since the usage will be predominantly read
     * a readwritelock is used.
     *
     * @param refresh
     * @return
     * @throws ProcessorProviderException
     */
    private Set<ProcessorInfo> getProcessorDescriptors(boolean refresh) throws ProcessorProviderException {
        processorDescriptorLock.readLock().lock();
        try {
            if (processorInfos.size() == 0 || refresh) {
                // drop the read lock because we cannot acquire the write lock while we hold a read lock
                processorDescriptorLock.readLock().unlock();

                try {
                    // if another thread acquires the writelock then this thread
                    // will block when it tries to re-acquire the read lock
                    // if this thread acquires the writelock then it populates the
                    // processorProvider Set
                    if (processorDescriptorLock.writeLock().tryLock()) {
                        try {
                            // always call clear() because we NEVER add to an existing Set
                            processorInfos.clear();
                            for (ProcessorProvider processorProvider : providerLoader) {
                                processorInfos.addAll(processorProvider.getProcessorInfos());
                            }
                        } finally {
                            processorDescriptorLock.writeLock().unlock();
                        }
                    }
                } finally {
                    // re-acquire the read lock
                    processorDescriptorLock.readLock().lock();
                }
            }
        }
        finally {
            processorDescriptorLock.readLock().unlock();
        }

        return processorInfos;
    }

    /**
     * Return TRUE if the described Processor can be created by an installed
     * ProcessorProvider.
     *
     * @param descriptor
     * @return
     */
    public boolean canCreateProcessor(ProcessorInfo descriptor) {
        return findProcessorProvider(descriptor) != null;
    }

    /**
     * Create a Processor instance from the Descriptor and the configuration
     * values.
     *
     * @param descriptor        a ProcessorDescriptor that uniquely identifies the Processor to create
     * @param processorSettings an extension of BaseSettings that provides access to the
     *                          configuration parameters. The CommandProvider SHOULD use
     *                          the bean naming convention to match configuration getters
     *                          to processor setters.
     * @return an instance of the identified Processor
     */
    @Override
    public RSProcessor createProcessor(final ProcessorInfo descriptor, final Object processorSettings) throws ProcessorProviderException {
        ProcessorProvider provider = findProcessorProvider(descriptor);
        if (provider == null) {
            throw new UnknownProcessorDescriptorException(descriptor);
        }

        return provider.createProcessor(descriptor, processorSettings);
    }

    /**
     * Find the ProcessorProvider that can create the described Processor.
     *
     * @param descriptor
     * @return
     */
    ProcessorProvider findProcessorProvider(ProcessorInfo descriptor) {
        for(ProcessorProvider provider : providerLoader) {
            try {
                if (provider.canCreateProcessor(descriptor)) {
                    return provider;
                }
            } catch (ProcessorProviderException e) {
                LOGGER.warn("ProcessorProviderException occurred in findProcessorProvider(" + descriptor.toString() + ")");
            }
        }

        return null;
    }
}
