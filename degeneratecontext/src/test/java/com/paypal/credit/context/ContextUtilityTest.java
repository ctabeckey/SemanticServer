package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ListType;
import com.paypal.credit.context.xml.ScopeType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by cbeckey on 2/4/16.
 */
public class ContextUtilityTest {

    // ====================================================================================
    // createInstanceFromStringValue tests
    // ====================================================================================
    @DataProvider
    public Object[][] createInstanceFromStringValueDataProvider() {
        return new Object[][] {
                new Object[]{Integer.class, "1", new Integer(1)},
                new Object[]{Integer.class, "-1", new Integer(-1)},
                new Object[]{Integer.class, "0", new Integer(0)},
                new Object[]{Float.class, "1.0", new Float(1.0)},
                new Object[]{Finteger.class, "1", new Finteger("1")},
        };
    }

    @Test(dataProvider = "createInstanceFromStringValueDataProvider")
    public void testCreateInstanceFromStringValue(final Class<?> clazz, final String value, final Object expectedValue)
            throws CannotCreateObjectFromStringException {
        Assert.assertEquals(ContextUtility.createInstanceFromStringValue(clazz, value), expectedValue);
    }

    /**
     * A simple class to test instance creation from a constructor.
     */
    public static class Finteger {
        private final Integer wrapped;

        public Finteger(final String s) throws NumberFormatException {
            wrapped = new Integer(s);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Finteger finteger = (Finteger) o;
            return Objects.equals(wrapped, finteger.wrapped);
        }

        @Override
        public int hashCode() {
            return Objects.hash(wrapped);
        }
    }

    // ====================================================================================
    // static boolean isApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedParameters)
    // ====================================================================================

    @DataProvider
    public Object[][] isApplicableConstructorDataProvider() throws NoSuchMethodException {
        return new Object[][] {
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{}),
                        (List<ConstructorArgType>)null,
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class}),
                        Arrays.asList(TestUtility.createConstructorArgType("value", null)),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Integer.class}),
                        Arrays.asList(TestUtility.createConstructorArgType("1", null)),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Number.class}),
                        Arrays.asList(TestUtility.createConstructorArgType("1", null)),
                        Boolean.FALSE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class, Integer.class}),
                        Arrays.asList(TestUtility.createConstructorArgType("value", null), TestUtility.createConstructorArgType("1", null)),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class, Number.class}),
                        Arrays.asList(TestUtility.createConstructorArgType("value", null), TestUtility.createConstructorArgType("1", null)),
                        Boolean.FALSE
                },
        };
    }

    @Test(dataProvider="isApplicableConstructorDataProvider")
    public void testIsApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedParameters, final Boolean expectedResult)
            throws BeanClassNotFoundException {
        Assert.assertEquals(ContextUtility.isApplicableConstructor(ctor, orderedParameters), expectedResult.booleanValue());
    }

    // ====================================================================================
    // static <T> Constructor<T> selectConstructor(
    //   final Class<T> beanClazz,
    //   final List<ConstructorArgType> orderedParameters)
    // ====================================================================================

    @DataProvider
    public Object[][] selectConstructorDataProvider() throws NoSuchMethodException {
        return new Object[][] {
                new Object[]{
                        ConstructorTestSubject.class,
                        (List<ConstructorArgType>)null,
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{})
                },
                new Object[]{
                        ConstructorTestSubject.class,
                        Arrays.asList(TestUtility.createConstructorArgType("1", null)),
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Integer.class})
                }
        };
    }

    @Test(dataProvider = "selectConstructorDataProvider")
    public <T> void testSelectConstructor(final Class<T> beanClazz, final List<ConstructorArgType> orderedParameters, final Constructor<T> expectCtor) throws BeanClassNotFoundException {
        Assert.assertEquals(ContextUtility.selectConstructor(beanClazz, orderedParameters), expectCtor);
    }

    // ====================================================================================
    // static List<ConstructorArgType> createOrderedParameterList(final BeanType beanType)
    // throws SparseArgumentListDetectedException {
    // ====================================================================================

    @DataProvider
    public Object[][] createOrderedParameterListDataProvider() {
        return new Object[][] {
                new Object[]{
                        TestUtility.addConstructorArg(
                                TestUtility.createBeanType("com.paypal.credit.context.ContextTest.ConstructorTestSubject", "id1", ScopeType.PROTOTYPE),
                                TestUtility.createConstructorArgType("hello", null)),
                        Arrays.asList(TestUtility.createConstructorArgType("hello", null))
                },
                new Object[]{
                        TestUtility.addConstructorArg(
                                TestUtility.addConstructorArg(
                                        TestUtility.createBeanType("com.paypal.credit.context.ContextTest.ConstructorTestSubject", "id1", ScopeType.PROTOTYPE),
                                        TestUtility.createConstructorArgType("hello", null)),
                                TestUtility.createConstructorArgType("world", new Integer(0))),
                        Arrays.asList(
                                TestUtility.createConstructorArgType("world", new Integer(0)),
                                TestUtility.createConstructorArgType("hello", null))
                }
                ,
                new Object[]{
                        TestUtility.addConstructorArg(
                                TestUtility.addConstructorArg(
                                        TestUtility.addConstructorArg(
                                                TestUtility.createBeanType("com.paypal.credit.context.ContextTest.ConstructorTestSubject", "id1", ScopeType.PROTOTYPE),
                                                TestUtility.createConstructorArgType("hello", null)),
                                        TestUtility.createConstructorArgType("world", new Integer(0))),
                                TestUtility.createConstructorArgType("peas", new Integer(2))),
                        Arrays.asList(
                                TestUtility.createConstructorArgType("world", new Integer(0)),
                                TestUtility.createConstructorArgType("hello", null),
                                TestUtility.createConstructorArgType("peas", new Integer(2)))
                }
        };
    }

    @Test(dataProvider = "createOrderedParameterListDataProvider")
    public void testCreateOrderedParameterList(final BeanType beanType, List<ConstructorArgType> expected)
    throws SparseArgumentListDetectedException {
        List<ConstructorArgType> actual = ContextUtility.createOrderedParameterList(beanType);
        Assert.assertEquals(actual.size(), expected.size());
        for (int index=0; index<expected.size(); ++index) {
            Assert.assertTrue(TestUtility.isEquals(actual.get(index), expected.get(index)));
        }
    }
}
