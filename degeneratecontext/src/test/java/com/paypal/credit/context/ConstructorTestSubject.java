package com.paypal.credit.context;

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

    public ConstructorTestSubject() {
        string1 = null;
        int1 = null;
        num1 = null;
        child = null;
    }

    public ConstructorTestSubject(String val1) {
        string1 = val1;
        int1 = null;
        num1 = null;
        child = null;
    }

    public ConstructorTestSubject(Number val1) {
        string1 = null;
        int1 = null;
        num1 = val1;
        child = null;
    }

    public ConstructorTestSubject(Integer val1) {
        string1 = null;
        int1 = val1;
        num1 = null;
        child = null;
    }

    public ConstructorTestSubject(String val1, Number val2) {
        string1 = val1;
        int1 = null;
        num1 = val2;
        child = null;
    }

    public ConstructorTestSubject(String val1, Integer val2) {
        string1 = val1;
        int1 = val2;
        num1 = null;
        child = null;
    }

    public ConstructorTestSubject(String val1, Integer val2, Number val3) {
        string1 = val1;
        int1 = val2;
        num1 = val3;
        child = null;
    }

    public ConstructorTestSubject(ConstructorTestSubject child) {
        string1 = null;
        int1 = null;
        num1 = null;
        this.child = child;
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
}
