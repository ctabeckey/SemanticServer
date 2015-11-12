package com.paypal.credit.test;

import com.paypal.credit.core.commandprocessor.RoutingToken;

import java.util.Objects;

/**
 * Created by cbeckey on 11/10/15.
 */
public class ProductTypeRoutingToken
implements RoutingToken {
    private final String productType;

    public ProductTypeRoutingToken(final String productType) {
        this.productType = productType;
    }

    @Override
    public int compareTo(final RoutingToken o) {
        if (this.equals(o)) {
            return 0;
        }

        if (o instanceof ProductTypeRoutingToken) {
            ProductTypeRoutingToken other = (ProductTypeRoutingToken)o;
            return this.productType.compareTo(other.productType);
        }

        return -1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductTypeRoutingToken that = (ProductTypeRoutingToken) o;
        return Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType);
    }
}
