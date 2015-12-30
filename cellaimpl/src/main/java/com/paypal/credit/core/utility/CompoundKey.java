package com.paypal.credit.core.utility;

import java.util.Objects;

/**
 * A (marker) interface to re-iterate the necessity of implementing hashCode() and equals()
 * in CompoundKey classes.
 */
public interface CompoundKey {
    boolean equals(final Object o);

    int hashCode();

}
