package com.paypal.credit.core.datasourceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToCreateServiceProviderImplementation;
import com.paypal.credit.core.datasourceprovider.exceptions.UnableToFindServiceProviderImplementation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class RootDataSourceProviderFactory
    implements DataSourceProviderFactory
{
    /**  */
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    /** The ONLY ServiceLoader for the ServiceProviderFactory type */
    private final ServiceLoader<DataSourceProviderFactory> serviceLoader;

    /** The Set of DataSourceDescription provided by all available factories */
    private final SortedSet<DataSourceDescription<?>> dataSourceDescriptions = new TreeSet<>();

    /** A cache of class instances mapped to the ClassLoader that they load from */
    private static final Map<ClassLoader, RootDataSourceProviderFactory> rootServiceProviderFactoryCache =
            new ConcurrentHashMap<>();

    /**
     *
     * @return
     */
    public static RootDataSourceProviderFactory getOrCreate() {
        return getOrCreate(Thread.currentThread().getContextClassLoader());
    }

    /**
     *
     * @param classLoader
     * @return
     */
    public static RootDataSourceProviderFactory getOrCreate(ClassLoader classLoader) {
        RootDataSourceProviderFactory serviceFactory = null;
        serviceFactory = rootServiceProviderFactoryCache.get(classLoader);
        if (serviceFactory == null) {
            // Note that there is a small chance that a service factory will get created
            // multiple times. The first copy will get dropped after the second copy
            // is inserted in the Map
            serviceFactory = new RootDataSourceProviderFactory(classLoader);
            rootServiceProviderFactoryCache.put(classLoader, serviceFactory);
        }

        return serviceFactory;
    }

    // ============================================================================
    // ServiceProviderFactory Implementation
    // ============================================================================

    /**  */
    private final static String PUBLISHER = RootDataSourceProviderFactory.class.getName();

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
                for (DataSourceProviderFactory providerFactory : this.serviceLoader) {
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
    private RootDataSourceProviderFactory(ClassLoader cl) {
        this.serviceLoader = ServiceLoader.load(DataSourceProviderFactory.class, cl);
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
	public final <S extends DataSourceProviderInterface> S createDataSource(
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
    private final Map<Class<?>, DataSourceProviderInterface> serviceInstanceCache = new ConcurrentHashMap<>();
    private <S extends DataSourceProviderInterface> S getOrCreateService(final DataSourceDescription<S> dsd)
            throws IllegalAccessException, InstantiationException {
        DataSourceProviderInterface result = null;

        result = serviceInstanceCache.get(dsd.getImplementingClass());
        if (result == null) {
            result = dsd.getServiceInterface().cast(dsd.getImplementingClass().newInstance());
            serviceInstanceCache.put(dsd.getImplementingClass(), result);
        }

        return (S)result;
    }

    public final DataSourceDescription findDataSource(
            Class<? extends DataSourceProviderInterface> dataSourceApi,
            RoutingToken routingToken) {
        for (DataSourceDescription dsd : getInstalledProviders()) {
            if (dsd.isApplicable(dataSourceApi, routingToken)) {
                return dsd;
            }
        }

        return null;
    }

    public final DataSourceDescription findDataSource(
            Class<? extends DataSourceProviderInterface> dataSourceApi,
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
            Class<? extends DataSourceProviderInterface> dataSourceApi,
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
            Class<? extends DataSourceProviderInterface> dataSourceApi,
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

    /**
     * This class describes the capabilities of a ServiceProvider implementation
     * in terms of the Service Interface and the Routing Token that the Service
     * can service.
     * If a single Service services multiple Service interfaces or Routing Token
     * then it will have multiple DataSourceDescription instances.
     */
    public final static class DataSourceDescription<S extends DataSourceProviderInterface>
    implements Comparable<DataSourceDescription>{
        /** the publisher, in case a client wants a specific implementation */
        private final String publisher;

        /** the version number, higher version numbers are preferred */
        private final int version;

        /** the service interface that this service implements  */
        private final Class<S> serviceInterface;

        /** the routing token that this service can route requests for */
        private final RoutingToken routingToken;

        /** the class that implements the service */
        private final Class<?> implementingClass;

        /**
         *
         * @param serviceInterface
         * @param routingToken
         * @param implementingClass
         */
        public DataSourceDescription(
                @NotNull final String publisher,
                @Min(0) final int version,
                @NotNull final Class<S> serviceInterface,
                @NotNull final RoutingToken routingToken,
                @NotNull final Class<?> implementingClass) {
            this.publisher = publisher;
            this.version = version;
            this.serviceInterface = serviceInterface;
            this.routingToken = routingToken;
            this.implementingClass = implementingClass;
        }

        public String getPublisher() {
            return publisher;
        }

        public int getVersion() {
            return version;
        }

        public final Class<S> getServiceInterface() {
            return serviceInterface;
        }

        public final RoutingToken getRoutingToken() {
            return routingToken;
        }

        public final Class<?> getImplementingClass() {
            return implementingClass;
        }

        /**
         *
         * @param serviceInterface
         * @param routingToken
         * @return
         */
        public boolean isApplicable(final Class<S> serviceInterface, final RoutingToken routingToken) {
            return this.getRoutingToken().equals(routingToken)
                    && this.getServiceInterface().equals(serviceInterface);
        }

        public boolean isApplicable(final Class<S> serviceInterface, final RoutingToken routingToken, final String publisher) {
            return isApplicable(serviceInterface, routingToken)
                    && publisher.equals(this.getPublisher());
        }

        public boolean isApplicable(final Class<S> serviceInterface, final RoutingToken routingToken, final int version) {
            return isApplicable(serviceInterface, routingToken)
                    && version == this.getVersion();
        }

        public boolean isApplicable(final Class<S> serviceInterface, final RoutingToken routingToken, final String publisher, final int version) {
            return isApplicable(serviceInterface, routingToken, publisher)
                    && version == this.getVersion();
        }

        public boolean isApplicableVersionAtLeast(final Class<S> serviceInterface, final RoutingToken routingToken, final String publisher, final int version) {
            return isApplicable(serviceInterface, routingToken, publisher)
                    && version <= this.getVersion();
        }

        /**
         * The natural ordering of DataSourceDescription is defined as:
         * Service Interface
         * RoutingToken
         * Version
         * Publisher
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(final DataSourceDescription o) {
            if (o == null) {
                throw new NullPointerException("DataSourceDescription.compareTo(null) is not permitted.");
            }
            if (this.equals(o)) {
                return 0;
            }

            // sort on ServiceInterface, first
            int result =
                    this.getServiceInterface().getName().compareTo(o.getServiceInterface().getName());
            if (result != 0) {
                return result;
            }

            // sort on RoutingToken, second
            if (this.getRoutingToken().equals(o.getRoutingToken())) {
                result = 0;
            } else {
                if (this.getRoutingToken().getClass().equals(o.getRoutingToken().getClass())) {
                    result = this.getRoutingToken().compareTo(o.getRoutingToken());
                } else {
                    result = this.getRoutingToken().getClass().getName().compareTo(o.getRoutingToken().getClass().getName());
                }
            }
            if (result != 0) {
                return result;
            }

            // sort on version, third
            result = o.getVersion() - this.getVersion();
            if (result != 0) {
                return result;
            }

            // sort by Publisher, fourth
            return this.getPublisher().compareTo(o.getPublisher());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataSourceDescription<?> that = (DataSourceDescription<?>) o;
            return getVersion() == that.getVersion() &&
                    Objects.equals(getPublisher(), that.getPublisher()) &&
                    Objects.equals(getServiceInterface(), that.getServiceInterface()) &&
                    Objects.equals(getRoutingToken(), that.getRoutingToken()) &&
                    Objects.equals(getImplementingClass(), that.getImplementingClass());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPublisher(), getVersion(), getServiceInterface(), getRoutingToken(), getImplementingClass());
        }

        /**
         *
         * @return
         */
        public String toString() {
            return String.format("%s V%d %s(%s)->(%s)",
                    getPublisher(),
                    getVersion(),
                    getServiceInterface().getName(),
                    getRoutingToken().toString(),
                    getImplementingClass().getName()
            );
        }
    }
}
