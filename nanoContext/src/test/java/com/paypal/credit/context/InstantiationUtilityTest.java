package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.ConstructorArgType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cbeckey on 2/4/16.
 */
public class InstantiationUtilityTest {

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
                new Object[]{Class.class, "java.lang.Object", java.lang.Object.class},
                new Object[]{String.class, "1.0", "1.0"},
        };
    }

    @Test(dataProvider = "createInstanceFromStringValueDataProvider")
    public void testCreateInstanceFromStringValue(final Class<?> clazz, final String value, final Object expectedValue)
            throws CannotCreateObjectFromStringException {
        Assert.assertEquals(InstantiationUtility.createInstanceFromStringValue(clazz, value, false), expectedValue);
    }

    // ====================================================================================
    // static boolean isApplicableConstructor(final Constructor<?> ctor, final List<ConstructorArgType> orderedParameters)
    // ====================================================================================

    // ====================================================================================
    // static <T> Constructor<T> selectConstructor(
    //   final Class<T> beanClazz,
    //   final List<ConstructorArgType> orderedParameters)
    // ====================================================================================

    @DataProvider
    public Object[][] createArgumentsDataProvider() {
        return new Object[][] {
                // test of no-arg method
                new Object[]{
                        Arrays.asList(),
                        new Class<?>[]{},
                        new Object[]{}
                },
                // test of one String argument
                new Object[]{
                        Arrays.asList(TestUtility.createConstructorArgType("hello", null)),
                        new Class<?>[]{String.class},
                        new Object[]{"hello"}
                },
                // test of two String argument
                new Object[]{
                        Arrays.asList(TestUtility.createConstructorArgType("hello", null), TestUtility.createConstructorArgType("world", null)),
                        new Class<?>[]{String.class, String.class},
                        new Object[]{"hello", "world"}
                },
                // test of one argument requiring conversion
                new Object[]{
                        Arrays.asList(TestUtility.createConstructorArgType("42", null)),
                        new Class<?>[]{Integer.class},
                        new Object[]{new Integer(42)}
                },
        };
    }

    @Test(dataProvider = "createArgumentsDataProvider")
    public void testCreateArguments(final List<ConstructorArgType> orderedParameters, final Class<?>[] parameterTypes, final Object[] expected) throws ContextInitializationException {
        //Object[] actual = ContextUtility.createArguments(orderedParameters, parameterTypes);
        //Assert.assertEquals(actual, expected);
    }

}
