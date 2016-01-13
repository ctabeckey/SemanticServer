package com.paypal.credit;

import com.paypal.credit.ReflectionUtilities;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * test the ProcessorUtilities methods
 */
public class ClassSeparationUtilitiesTest {
    // Classes and interface with various derivative relationships used as test subjects
    interface A { }                                     // a base interface
    interface B extends A { }                           // a derived interface
    interface C extends B { }                           // a second order derived interface
    interface D extends B { }                           // another second order derived interface
    interface E extends D { }                           // a third order derived interface
    interface F extends E { }                           // a fourth order derived interface

    static class X { }                                  // a base class
    static class Y extends X { }                        // a first order derivative
    static class Z extends Y implements C, F { }        // a second order derivative with interfaces
    static class W extends Y implements F { }           // the same second order derivative with only one interface
    static class V { }                                  // an unrelated class


    @DataProvider
    public Object[][] testClassSeparationData() {
        return new Object[][] {
                new Object[]{X.class, Y.class, -1},
                new Object[]{X.class, Z.class, -2},
                new Object[]{X.class, W.class, -2},
                new Object[]{X.class, V.class, Integer.MAX_VALUE},

                new Object[]{C.class, Z.class, -1},         // test an interface
                new Object[]{A.class, Z.class, -3},         // test an interface

                new Object[]{Number.class, Object.class, 1},
                new Object[]{Byte.class, Object.class, 2},
                new Object[]{String.class, CharSequence.class, 1},         // test an interface
        };
    }

    @Test(dataProvider = "testClassSeparationData")
    public void testClassSeparation(Class<?> base, Class<?> derivative, int expected) {
        Assert.assertEquals(ReflectionUtilities.degreesOfSeparation(base, derivative), expected);
    }

    @DataProvider
    public Object[][] testClassArraySeparationData() {
        return new Object[][] {
                new Object[]{new Class<?>[]{Object.class, Object.class}, new Class<?>[]{Boolean.class, Boolean.class}, -2},
                new Object[]{new Class<?>[]{Object.class, Object.class}, new Class<?>[]{Byte.class, Byte.class}, -4},
                new Object[]{new Class<?>[]{Number.class, Object.class}, new Class<?>[]{Byte.class, Byte.class}, -3},
                new Object[]{new Class<?>[]{String.class, Object.class}, new Class<?>[]{Byte.class, Boolean.class}, Integer.MAX_VALUE},
                new Object[]{new Class<?>[]{CharSequence.class, Object.class}, new Class<?>[]{String.class, Boolean.class}, -2},
        };
    }

    @Test(dataProvider = "testClassArraySeparationData")
    public void testClassArraySeparation(Class<?>[] base, Class<?>[] derivative, int expected) {
        Assert.assertEquals(ReflectionUtilities.degreesOfSeparation(base, derivative), expected);
    }

    @Test
    public void testForShortestPath() {
        Assert.assertEquals(ReflectionUtilities.degreesOfSeparation(A.class, Z.class), -3);
        Assert.assertEquals(ReflectionUtilities.degreesOfSeparation(A.class, W.class), -5);
    }

}
