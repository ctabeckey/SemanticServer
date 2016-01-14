package com.paypal.credit.utility;

import java.util.Objects;

/**
 * A Generic Type that may be used as a three-member compound key. This class
 * correctly supports .hashCode() and .equals() so may be used as a Set element or Map key.
 */
public class FourMemberCompoundKey<A, B, C, D>
implements CompoundKey {
    private final A a;
    private final B b;
    private final C c;
    private final D d;

    public FourMemberCompoundKey(A a, B b, C c, D d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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

    public D getD() {
        return d;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FourMemberCompoundKey<?, ?, ?, ?> that = (FourMemberCompoundKey<?, ?, ?, ?>) o;
        return Objects.equals(getA(), that.getA()) &&
                Objects.equals(getB(), that.getB()) &&
                Objects.equals(getC(), that.getC()) &&
                Objects.equals(getD(), that.getD());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getA(), getB(), getC(), getD());
    }
}
