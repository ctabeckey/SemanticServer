package com.paypal.credit.core.datasourceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class RootDataSourceProviderImpl
    implements RootDataSourceProvider, DataSourceProvider
{
    /**  */
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    /** The ONLY ServiceLoader for the ServiceProviderFactory type */
    private final ServiceLoader<DataSourceProvider> serviceLoader;

    /** The Set of DataSourceDescription provided by all available factories */
    private final SortedSet<DataSourceDescription<?>> dataSourceDescriptions = new TreeSet<>();

    /** A cache of class instances mapped to the ClassLoader that they load from */
    private static final Map<ClassLoader, RootDataSourceProviderImpl> rootServiceProviderFactoryCache =
            new ConcurrentHashMap<>();

    /**
     *
     * @return
     */
    public static RootDataSourceProviderImpl getOrCreate() {
        return getOrCreate(Thread.currentThread().getContextClassLoader());
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public static RootDataSourceProviderImpl getOrCreate(ClassLoader classLoader) {
        RootDataSourceProviderImpl serviceFactory = null;
        serviceFactory = rootServiceProviderFactoryCache.get(classLoader);
        if (serviceFactory == null) {
            // Note that there is a small chance that a service factory will get created
            // multiple times. The first copy will get dropped after the second copy
            // is inserted in the Map
            serviceFactory = new RootDataSourceProviderImpl(classLoader);
            rootServiceProviderFactoryCache.put(classLoader, serviceFactory);
        }

        return serviceFactory;
    }

    // ============================================================================
    // ServiceProviderFactory Implementation
    // ============================================================================

    /**  */
    private final static String PUBLISHER = RootDataSourceProviderImpl.class.getName();

    /**
     * The publisher of the service providers.
     * @return an identifier of the publisher, usable only as a human readable String
     */
    @Override
    public String getPublisher() {
        return PUBLISHER;
    }

    /**
     * Return the Set of all available DataSourceDescription.
     * Overriding methods should return the Set of DataSourceDescription
     * that the derived implementation provides.
     *
     * @return the Set of installed DataSourceDescription
     */
    @Override
    public final Set<DataSourceDescription<?>> getInstalledProviders() {
        synchronized (this.dataSourceDescriptions) {
            if (this.dataSourceDescriptions.isEmpty()) {
                for (DataSourceProvider providerFactory : this.serviceLoader) {
                    this.dataSourceDescriptions.addAll(providerFactory.getInstalledProviders());
                }
            }
        }
        return this.dataSourceDescriptions;
    }

    // ============================================================================
    // RootServiceProviderFactoryImpl Exclusive Function
    // ============================================================================

    /**
     * Private constructor, use factory methods
     */
    private RootDataSourceProviderImpl(ClassLoader cl) {
        this.serviceLoader = ServiceLoader.load(DataSourceProvider.class, cl);
    }

    /**
     * Clear caches and reload the service factories.
     * Use only with caution, will cause a slowdown.
     */
    public final void reload() {
        synchronized (this.dataSourceDescriptions) {
            this.dataSourceDescriptions.clear();
            this.serviceLoader.reload();
        }
    }

    /**
     * Return a human readable description of the installed Provider implementations.
     *
     * @return
     */
	public final String[] getInstalledProviderDescriptions() {
        String[] descs = new String[getInstalledProviders() == null ? 0 : getInstalledProviders().size()];
        int index = 0;
        for(DataSourceDescription dsd : getInstalledProviders()) {
            descs[index] = dsd.toString();
            index++;
        }

        return descs;
    }

    /**
     *
     * @return
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPublisher());
        sb.append(LINE_SEPARATOR);
        for(String desc : getInstalledProviderDescriptions()) {
            sb.append(desc);
            sb.append(LINE_SEPARATOR);
        }

        return sb.toString();
    }

	/**
	 *
	 * @param dataSourceApi
     * @param routingToken
	 * @param dataSourceProviderExceptionHandlers
	 * @param <S>
	 * @return
	 */
	@Override
	public final <S> S createDataSource(
			Class<S> dataSourceApi,
            RoutingToken routingToken,
			DataSourceProviderExceptionHandler... dataSourceProviderExceptionHandlers)
    throws UnableToFindServiceProviderImplementation, UnableToCreateServiceProviderImplementation {
        DataSourceDescription<S> dsd = findDataSource(dataSourceApi, routingToken);

        if (dsd == null) {
            throw new UnableToFindServiceProviderImplementation(dataSourceApi, routingToken);
        }

        S result = null;
        try {
            result = getOrCreateService(dsd);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new UnableToCreateServiceProviderImplementation(
                    dataSourceApi, routingToken, dsd.getServiceInterface()
            );
        }
        return result;
    }

    // ===============================================================================================
    // Service Cache Implementation
    // ===============================================================================================
    private final Map<Class<?>, Object> serviceInstanceCache = new ConcurrentHashMap<>();
    private <S> S getOrCreateService(final DataSourceDescription<S> dsd)
            throws IllegalAccessException, InstantiationException {
        Object result = null;

        result = serviceInstanceCache.get(dsd.getImplementingClass());
        if (result == null) {
            result = dsd.getServiceInterface().cast(dsd.getImplementingClass().newInstance());
            serviceInstanceCache.put(dsd.getImplementingClass(), result);
        }

        return (S)result;
    }

    public final DataSourceDescription findDataSource(
            Class<?> dataSourceApi,
            RoutingToken routingToken) {
        for (DataSourceDescription dsd : getInstalledProviders()) {
            if (dsd.isApplicable(dataSourceApi, routingToken)) {
                return dsd;
            }
        }

        return null;
    }

    public final DataSourceDescription findDataSource(
            Class<?> dataSourceApi,
            RoutingToken routingToken,
            int version) {
        for (DataSourceDescription dsd : getInstalledProviders()) {
            if (dsd.isApplicable(dataSourceApi, routingToken, version)) {
                return dsd;
            }
        }

        return null;
    }

    public final DataSourceDescription findDataSource(
            Class<?> dataSourceApi,
            RoutingToken routingToken,
            String publisher) {
        for (DataSourceDescription dsd : getInstalledProviders()) {
            if (dsd.isApplicable(dataSourceApi, routingToken, publisher)) {
                return dsd;
            }
        }

        return null;
    }

    public final DataSourceDescription findDataSource(
            Class<?> dataSourceApi,
            RoutingToken routingToken,
            String publisher,
            int version) {
        for (DataSourceDescription dsd : getInstalledProviders()) {
            if (dsd.isApplicable(dataSourceApi, routingToken, publisher, version)) {
                return dsd;
            }
        }

        return null;
    }

}
