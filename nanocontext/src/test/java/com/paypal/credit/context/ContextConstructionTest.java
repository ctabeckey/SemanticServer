package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.ScopeType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

/**
 * tests of Context Construction
 */
public class ContextConstructionTest {

    // ================================================================================================
    // Test contexts with no dependencies between beans
    // ================================================================================================
    @DataProvider
    public Object[][] contextNoDependenciesTestDataProvider() {
        return new Object[][] {
                new Object[]{
                        "OneBeanContext.xml",
                        new BeanSpec[]{
                                new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON)
                        }
                },
                new Object[]{
                        "OneBeanContextWithCtorArg.xml",
                        new BeanSpec[]{
                                new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON)
                        }
                },
                new Object[]{
                        "OnePrototypeBeanContext.xml",
                        new BeanSpec[]{new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.PROTOTYPE)}
                },
                new Object[]{
                        "TwoBeanContext.xml",
                        new BeanSpec[]{
                                new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON),
                                new BeanSpec("beanTwo", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON)
                        }
                },
                new Object[]{
                        "OneRemoteBeanContext.xml",
                        new BeanSpec[]{
                                new BeanSpec("nullInStream", null, ScopeType.SINGLETON)
                        }
                },
        };
    }

    @Test(dataProvider = "contextNoDependenciesTestDataProvider")
    public void createContextNoDependenciesTest(final String resourceName, final BeanSpec[] expectedBeans)
            throws JAXBException, ContextInitializationException {
        Context ctx = new ContextFactory()
                .with(getClass().getClassLoader().getResourceAsStream(resourceName))
                .build();
        Assert.assertNotNull(ctx);

        for (int index = 0; index < expectedBeans.length; ++index) {
            BeanSpec beanSpec = expectedBeans[index];

            Object beanInstance = validateBeanExistenceAndType(ctx, beanSpec);
            Object secondBeanInstance = validateBeanExistenceAndType(ctx, beanSpec);

            // validate that the SINGLETON/PROTOTYPE scope is working correctly
            switch(expectedBeans[index].getScope()) {
                case PROTOTYPE:
                    Assert.assertTrue(beanInstance != secondBeanInstance);
                    break;
                case SINGLETON:
                    Assert.assertTrue(beanInstance == secondBeanInstance);
                    break;
                default:
                    break;
            }
        }
    }

    // ================================================================================================
    // Test contexts with child dependencies in constructor args
    // ================================================================================================
    @DataProvider
    public Object[][] contextChildDependenciesTestDataProvider() {
        return new Object[][]{
                new Object[]{
                        "OneBeanWithChildContext.xml",
                        new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON),
                        new String[]{"getChild"}
                },
                new Object[]{
                        "OneBeanWithReferencedChildContext.xml",
                        new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON),
                        new String[]{"getChild"}
                },
        };
    }

    @Test(dataProvider = "contextChildDependenciesTestDataProvider")
    public void createContextChildDependenciesTest(final String resourceName, final BeanSpec expectedTopLevelBean, String[] nonNullChildMethods)
            throws JAXBException, ContextInitializationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Context ctx = new ContextFactory()
                .with(getClass().getClassLoader().getResourceAsStream(resourceName))
                .build();
        Assert.assertNotNull(ctx);

        Object topLevelBean = ctx.getBean(expectedTopLevelBean.getIdentifier(), expectedTopLevelBean.getType());
        Assert.assertNotNull(topLevelBean);

        if (nonNullChildMethods != null) {
            Class<?> topLevelBeanClass = topLevelBean.getClass();
            for (String methodName : nonNullChildMethods) {
                Method method = topLevelBeanClass.getMethod(methodName, (Class<?>[])null);
                Assert.assertNotNull(method.invoke(topLevelBean, (Object[])null));
            }
        }
    }

    // ================================================================================================
    // Test contexts with a list of String in constructor args
    // ================================================================================================
    @DataProvider
    public Object[][] simpleContextListsTestDataProvider() {
        return new Object[][]{
                new Object[]{
                        "OneBeanWithListContext.xml",
                        new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON),
                        "getStrings",
                        new String[]{"42", "655321"}
                },
        };
    }
    @Test(dataProvider = "simpleContextListsTestDataProvider")
    public void createListsTest(final String resourceName, final BeanSpec expectedTopLevelBean,
                                final String accessorMethodName, final String[] expectedValues)
            throws JAXBException, ContextInitializationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Context ctx = new ContextFactory()
                .with(getClass().getClassLoader().getResourceAsStream(resourceName))
                .build();
        Assert.assertNotNull(ctx);

        Object topLevelBean = ctx.getBean(expectedTopLevelBean.getIdentifier(), expectedTopLevelBean.getType());
        Assert.assertNotNull(topLevelBean);

        if (expectedValues != null) {
            Method accessorMethod = topLevelBean.getClass().getMethod(accessorMethodName, (Class<?>[])null);
            String[] values = (String[])accessorMethod.invoke(topLevelBean, (Object[])null);
            Assert.assertEquals(values.length, expectedValues.length);
            for (int index = 0; index < values.length; ++index) {
                Assert.assertTrue(expectedValues[index].equals(values[index]));
            }
        }
    }

    // ================================================================================================
    // Test contexts with list of beans in constructor args
    // ================================================================================================
    @DataProvider
    public Object[][] complexContextListsTestDataProvider() {
        return new Object[][]{
                new Object[]{
                        "OneBeanWithListComplexContext.xml",
                        new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON),
                        "getChildren",
                        new ConstructorTestSubject[] {
                                new ConstructorTestSubject(1),
                                new ConstructorTestSubject(2)
                        }
                },
        };
    }

    @Test(dataProvider = "complexContextListsTestDataProvider")
    public void createComplexListsTest(final String resourceName, final BeanSpec expectedTopLevelBean,
                                final String accessorMethodName, final ConstructorTestSubject[] expectedValues)
            throws JAXBException, ContextInitializationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Context ctx = new ContextFactory()
                .with(getClass().getClassLoader().getResourceAsStream(resourceName))
                .build();
        Assert.assertNotNull(ctx);

        Object topLevelBean = ctx.getBean(expectedTopLevelBean.getIdentifier(), expectedTopLevelBean.getType());
        Assert.assertNotNull(topLevelBean);

        // if expected values are provided, then call the method on the top level bean that will
        // obtain the actual values and compare them for .equals()
        if (expectedValues != null) {
            // the name of the method to call within the top level bean
            Method accessorMethod = topLevelBean.getClass().getMethod(accessorMethodName, (Class<?>[])null);
            // the type of the expected values
            Class<?> expectedValueType = expectedValues.getClass();
            // the actual values resulting from the method call
            Object[] values = (ConstructorTestSubject[])accessorMethod.invoke(topLevelBean, (Object[])null);

            Assert.assertTrue(expectedValueType.isInstance(values),
                    String.format("%s.%s() results are not of the expected type.", topLevelBean.getClass().getSimpleName(), accessorMethodName)
            );
            Assert.assertEquals(values.length, expectedValues.length);
            for (int index = 0; index < values.length; ++index) {
                ConstructorTestSubject expectedValue = expectedValues[index];
                Object actualValue = values[index];
                Assert.assertEquals(actualValue, expectedValue);
            }
        }
    }

    // ===========================
    @DataProvider
    public Object[][] contextHierarchyTestDataProvider() {
        return new Object[][] {
                new Object[]{
                        new String[]{"OneBeanContext.xml", "OneRemoteBeanContext.xml"},
                        new BeanSpec[]{
                                new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON)
                        }
                },
                new Object[]{
                        new String[]{"OneBeanContext.xml", "TwoBeanContext.xml"},
                        new BeanSpec[]{
                                new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON),
                                new BeanSpec("beanTwo", com.paypal.credit.context.ConstructorTestSubject.class, ScopeType.SINGLETON)
                        }
                }
        };
    }

    @Test(dataProvider = "contextHierarchyTestDataProvider")
    public void createContextHierarchiesTest(final String[] resourceNames, final BeanSpec[] expectedBeans)
            throws JAXBException, ContextInitializationException {

        // build a hierarchy of context, maintaining a reference to only the last one
        Context currentContext = null;
        for (String resourceName : resourceNames) {
            ContextFactory ctxFactory = new ContextFactory()
                    .with(getClass().getClassLoader().getResourceAsStream(resourceName));
            if (currentContext != null) {
                ctxFactory.withParentContext(currentContext);
            }
            currentContext = ctxFactory.build();

            Assert.assertNotNull(currentContext);
        }

        for (BeanSpec beanSpec : expectedBeans) {
            validateBeanExistenceAndType(currentContext, beanSpec);
        }
    }

    /**
     * Validates that a bean exists in gthe context and that its type is compatible with the
     * expected type.
     *
     * @param currentContext
     * @param beanSpec
     * @return the bean instance or null
     *
     * @throws ContextInitializationException
     */
    private Object validateBeanExistenceAndType(Context currentContext, BeanSpec beanSpec) throws ContextInitializationException {
        Object beanInstance = null;

        if (beanSpec.getIdentifier() != null) {
            beanInstance = currentContext.getBean(beanSpec.getIdentifier(), beanSpec.getType());
        } else {
            beanInstance = currentContext.getBean(beanSpec.getType());
        }
        Assert.assertNotNull( beanInstance );
        ProtectionDomain pd = beanInstance.getClass().getProtectionDomain();
        Assert.assertNotNull(pd);

        if (beanSpec.getType() != null) {
            Assert.assertTrue(beanSpec.getType().isAssignableFrom(beanInstance.getClass()));
        }

        return beanInstance;
    }


    // ===========================

    /**
     *
     */
    private class BeanSpec {
        private final String identifier;
        private final Class<?> type;
        private final ScopeType scope;

        public BeanSpec(final String identifier, final Class<?> type, final ScopeType scope) {
            this.identifier = identifier;
            this.type = type;
            this.scope = scope;
        }

        public String getIdentifier() {
            return identifier;
        }

        public Class<?> getType() {
            return type;
        }

        public ScopeType getScope() {
            return scope;
        }
    }
}
