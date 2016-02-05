package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

/**
 * Created by cbeckey on 2/5/16.
 */
public class ContextConstructionTest {

    @DataProvider
    public Object[][] contextTestDataProvider() {
        return new Object[][] {
                new Object[]{
                        "OneBeanContext.xml",
                        new BeanSpec[]{new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class)}
                },
                new Object[]{
                        "OneBeanContextWithCtorArg.xml",
                        new BeanSpec[]{new BeanSpec("beanOne", com.paypal.credit.context.ConstructorTestSubject.class)}
                },
        };
    }

    @Test(dataProvider = "contextTestDataProvider")
    public void createContextTest(final String resourceName, final BeanSpec[] expectedBeans)
            throws JAXBException, ContextInitializationException {
        Context ctx = Context.create(getClass().getClassLoader().getResourceAsStream(resourceName));
        Assert.assertNotNull(ctx);

        for (int index = 0; index < expectedBeans.length; ++index) {
            if (expectedBeans[index].getIdentifier() != null) {
                Assert.assertNotNull(ctx.getBean(expectedBeans[index].getIdentifier(), expectedBeans[index].getType()));
            }
            Assert.assertNotNull( ctx.getBean(expectedBeans[index].getType()) );
            Assert.assertTrue(expectedBeans[index].getType().isAssignableFrom(ctx.getBean(expectedBeans[index].getType()).getClass()));
        }
    }

    /**
     *
     */
    private class BeanSpec {
        private final String identifier;
        private final Class<?> type;

        public BeanSpec(final String identifier, final Class<?> type) {
            this.identifier = identifier;
            this.type = type;
        }

        public String getIdentifier() {
            return identifier;
        }

        public Class<?> getType() {
            return type;
        }
    }
}
