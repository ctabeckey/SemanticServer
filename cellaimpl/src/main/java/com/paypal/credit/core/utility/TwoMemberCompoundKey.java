package com.paypal.credit.core.utility;

import java.util.Objects;

/**
 * A Generic Type that may be used as a two-member compound key. This class
 * correctly supports .hashCode() and .equals() so may be used as a Set element or Map key.
 */
public class TwoMemberCompoundKey<A, B>
implements CompoundKey {
    private final A a;
    private final B b;

    public TwoMemberCompoundKey(A a, B b){
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoMemberCompoundKey<?, ?> that = (TwoMemberCompoundKey<?, ?>) o;
        return Objects.equals(getA(), that.getA()) &&
                Objects.equals(getB(), that.getB());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getA(), getB());
    }
}
