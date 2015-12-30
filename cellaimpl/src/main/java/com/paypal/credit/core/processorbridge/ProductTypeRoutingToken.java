package com.paypal.credit.core.processorbridge;

import com.paypal.credit.core.commandprocessor.RoutingToken;
import com.paypal.credit.core.utility.ParameterCheckUtility;

import java.util.Objects;

/**
 * Created by cbeckey on 11/10/15.
 */
public class ProductTypeRoutingToken
implements RoutingToken {
    private final String productType;

    public ProductTypeRoutingToken(final String productType) {
        ParameterCheckUtility.checkParameterNotNull(productType, "productType");
        this.productType = productType;
    }

    public String getProductType() {
        return productType;
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
