package com.paypal.credit.context;

import java.util.Arrays;
import java.util.Objects;

/**
 * A class whose function is to make it apparent which constructor was called to
 * create the instance.
 * This is used to test that the Context is calling the expected constructor.
 */
public class ConstructorTestSubject {
    private final String string1;
    private final Integer int1;
    private final Number num1;
    private final ConstructorTestSubject child;
    private final String[] strings1;
    private final ConstructorTestSubject[] children;

    public ConstructorTestSubject() {
        string1 = null;
        int1 = null;
        num1 = null;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(String val1) {
        string1 = val1;
        int1 = null;
        num1 = null;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(Number val1) {
        string1 = null;
        int1 = null;
        num1 = val1;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(Integer val1) {
        string1 = null;
        int1 = val1;
        num1 = null;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(String val1, Number val2) {
        string1 = val1;
        int1 = null;
        num1 = val2;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(String val1, Integer val2) {
        string1 = val1;
        int1 = val2;
        num1 = null;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(String val1, Integer val2, Number val3) {
        string1 = val1;
        int1 = val2;
        num1 = val3;
        child = null;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(ConstructorTestSubject child) {
        string1 = null;
        int1 = null;
        num1 = null;
        this.child = child;
        strings1 = null;
        this.children = null;
    }

    public ConstructorTestSubject(final String[] strings) {
        string1 = null;
        int1 = null;
        num1 = null;
        this.child = null;
        strings1 = strings;
        this.children = null;
    }

    public ConstructorTestSubject(final ConstructorTestSubject[] children) {
        this.string1 = null;
        this.int1 = null;
        this.num1 = null;
        this.child = null;
        this.strings1 = null;
        this.children = children;
    }

    public String getString1() {
        return string1;
    }

    public Integer getInt1() {
        return int1;
    }

    public Number getNum1() {
        return num1;
    }

    public ConstructorTestSubject getChild() {
        return child;
    }

    public String[] getStrings() {
        return this.strings1;
    }

    public ConstructorTestSubject[] getChildren() {
        return children;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstructorTestSubject that = (ConstructorTestSubject) o;
        return Objects.equals(getString1(), that.getString1()) &&
                Objects.equals(getInt1(), that.getInt1()) &&
                Objects.equals(getNum1(), that.getNum1()) &&
                Objects.equals(getChild(), that.getChild()) &&
                Arrays.equals(strings1, that.strings1) &&
                Arrays.equals(getChildren(), that.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getString1(), getInt1(), getNum1(), getChild(), strings1, getChildren());
    }
}
