package com.paypal.credit.core.datasourceprovider;

import com.paypal.credit.core.commandprocessor.RoutingToken;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

/**
 * The interface provided by Data Source Providers to make the data sources available to
 * the application framework.
 */
public interface DataSourceProvider {
    /**
     *
     * @return
     */
    String getPublisher();

    /**
     *
     * @return
     */
    Set<DataSourceProvider.DataSourceDescription<?>> getInstalledProviders();

    /**
     * This class describes the capabilities of a ServiceProvider implementation
     * in terms of the Service Interface and the Routing Token that the Service
     * can service.
     * If a single Service services multiple Service interfaces or Routing Token
     * then it will have multiple DataSourceDescription instances.
     */
    final class DataSourceDescription<S>
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