package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.ConstructorArgType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cbeckey on 2/8/16.
 */
public class ContextTest {
    // ====================================================================================
    // static Object[] createArguments(final List<ConstructorArgType> orderedParameters, final Class<?>[] parameterTypes)
    // throws ContextInitializationException
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
