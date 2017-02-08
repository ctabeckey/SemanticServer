package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;
import com.paypal.credit.context.xml.ReferenceType;
import com.paypal.credit.context.xml.ScopeType;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cbeckey on 2/8/16.
 */
public class ContextTest {
    private Context emptyCtx;
    private PropertyFactory propertyFactory;

    @BeforeTest
    public void beforeTest() throws ContextInitializationException {
        emptyCtx = new ContextFactory()
                .build();
        propertyFactory = new PropertyFactory(emptyCtx);
    }

    @DataProvider
    public Object[][] isApplicableConstructorDataProvider() throws NoSuchMethodException {
        return new Object[][] {
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{}),
                        (List<AbstractProperty>)null,
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class}),
                        Arrays.asList(propertyFactory.createConstant("value")),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Integer.class}),
                        Arrays.asList(propertyFactory.createConstant("1")),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Number.class}),
                        Arrays.asList(propertyFactory.createConstant("1")),
                        Boolean.FALSE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class, Integer.class}),
                        Arrays.asList(
                                propertyFactory.createConstant("value"),
                                propertyFactory.createConstant("1")
                        ),
                        Boolean.TRUE
                },
                new Object[]{
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{String.class, Number.class}),
                        Arrays.asList(
                                propertyFactory.createConstant("value"),
                                propertyFactory.createConstant("1")
                        ),
                        Boolean.FALSE
                },
        };
    }

    @Test(dataProvider="isApplicableConstructorDataProvider")
    public void testIsApplicableConstructor(final Constructor<?> ctor, final List<AbstractProperty> parameters, final Boolean expectedResult)
            throws ContextInitializationException {
        Assert.assertEquals(InstantiationUtility.isApplicableConstructor(ctor, parameters), expectedResult.booleanValue());
    }

    @DataProvider
    public Object[][] selectConstructorDataProvider() throws NoSuchMethodException, ContextInitializationException {
        return new Object[][] {
                new Object[]{
                        ConstructorTestSubject.class,
                        (List<AbstractProperty>)null,
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{})
                },
                new Object[]{
                        ConstructorTestSubject.class,
                        Arrays.asList(
                                propertyFactory.createConstant("1")
                        ),
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{Integer.class})
                },
                new Object[]{
                        ConstructorTestSubject.class,
                        Arrays.asList(
                                propertyFactory.createBeanInstanceFactory(
                                    TestUtility.createBeanType(ConstructorTestSubject.class.getName(), null, ScopeType.SINGLETON)
                                )
                        ),
                        ConstructorTestSubject.class.getDeclaredConstructor(new Class<?>[]{ConstructorTestSubject.class})
                }
        };
    }

    @Test(dataProvider = "selectConstructorDataProvider")
    public <T> void testSelectConstructor(final Class<T> beanClazz, final List<AbstractProperty> parameters, final Constructor<T> expectCtor) throws ContextInitializationException {
        Assert.assertEquals(InstantiationUtility.selectConstructor(beanClazz, parameters), expectCtor);
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
            throws ContextInitializationException {
        List<ConstructorArgType> actual = propertyFactory.createOrderedParameterList(beanType);
        Assert.assertEquals(actual.size(), expected.size());
        for (int index=0; index<expected.size(); ++index) {
            Assert.assertTrue(TestUtility.isEquals(actual.get(index), expected.get(index)));
        }
    }
}
