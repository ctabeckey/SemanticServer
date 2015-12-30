package com.paypal.credit.core.utility;

import java.util.Objects;

/**
 * A Generic Type that may be used as a three-member compound key. This class
 * correctly supports .hashCode() and .equals() so may be used as a Set element or Map key.
 */
public class ThreeMemberCompoundKey<A, B, C>
implements CompoundKey {
    private final A a;
    private final B b;
    private final C c;

    public ThreeMemberCompoundKey(A a, B b, C c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreeMemberCompoundKey<?, ?, ?> that = (ThreeMemberCompoundKey<?, ?, ?>) o;
        return Objects.equals(getA(), that.getA()) &&
                Objects.equals(getB(), that.getB()) &&
                Objects.equals(getC(), that.getC());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getA(), getB(), getC());
    }
}
