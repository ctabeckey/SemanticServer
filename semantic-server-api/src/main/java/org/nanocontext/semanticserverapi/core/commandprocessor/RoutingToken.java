package org.nanocontext.semanticserverapi.core.commandprocessor;

import java.io.Serializable;

/**
 * This interface is required for any request that will be routed
 * Object identifiers may implement this interface to acquire
 * the capability to be routed or an implementation of this class may be otherwise
 * attached to a request to provide the information needed for routing.
 *
 * NOTE .equals(), .hashCode() and compareTo() must be correctly and consistently
 * implemented by the realizing classes.
 */
public interface RoutingToken
extends Serializable, Comparable<RoutingToken>
{
    /**
     * Must implement .equals() according to the contract here:
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
     *
     * @return true if that instance is the same destination as this instance
     */
	boolean equals(Object that);

    /**
     * Must return a valid hash code according to the contract here:
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#hashCode--
     *
     * @return a hashCode as an integer
     */
    int hashCode();
}
