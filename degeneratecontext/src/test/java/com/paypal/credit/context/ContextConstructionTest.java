package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.ScopeType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cbeckey on 2/5/16.
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
        };
    }

    @Test(dataProvider = "contextNoDependenciesTestDataProvider")
    public void createContextNoDependenciesTest(final String resourceName, final BeanSpec[] expectedBeans)
            throws JAXBException, ContextInitializationException {
        Context ctx = Context.create(getClass().getClassLoader().getResourceAsStream(resourceName));
        Assert.assertNotNull(ctx);

        for (int index = 0; index < expectedBeans.length; ++index) {
            Object beanInstance = null;
            if (expectedBeans[index].getIdentifier() != null) {
                beanInstance = ctx.getBean(expectedBeans[index].getIdentifier(), expectedBeans[index].getType());
            } else {
                beanInstance = ctx.getBean(expectedBeans[index].getType());
            }
            Assert.assertNotNull( beanInstance );
            Assert.assertTrue(expectedBeans[index].getType().isAssignableFrom(beanInstance.getClass()));

            Object secondBeanInstance = null;
            if (expectedBeans[index].getIdentifier() != null) {
                secondBeanInstance = ctx.getBean(expectedBeans[index].getIdentifier(), expectedBeans[index].getType());
            } else {
                secondBeanInstance = ctx.getBean(expectedBeans[index].getType());
            }

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
        Context ctx = Context.create(getClass().getClassLoader().getResourceAsStream(resourceName));
        Assert.assertNotNull(ctx);

        Object topLevelBean = ctx.getBean(expectedTopLevelBean.getIdentifier(), expectedTopLevelBean.getType());
        Assert.assertNotNull(topLevelBean);

        if (nonNullChildMethods != null) {
            Class<?> topLevelBeanClass = topLevelBean.getClass();
            for (String methodName : nonNullChildMethods) {
                Method method = topLevelBeanClass.getMethod(methodName, null);
                Assert.assertNotNull(method.invoke(topLevelBean, null));
            }
        }
    }

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
