package com.paypal.credit;

import com.paypal.credit.processors.exceptions.NoApplicableConstructorException;
import com.paypal.credit.processors.exceptions.ProcessorInstantiationException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by cbeckey on 1/12/16.
 */
public class ClassMemberSelectionTests {
    @DataProvider
    public Object[][] findSettingsData() throws IntrospectionException {
        return new Object[][] {
                new Object[] {String.class, Introspector.getBeanInfo(PerfectSettingsBean.class), String.class, "getStringProperty", "setStringProperty"},
                new Object[] {Integer.class, Introspector.getBeanInfo(PerfectSettingsBean.class), Integer.class, "getIntProperty", "setIntProperty"}
        };
    }

    @Test(dataProvider = "findSettingsData")
    public void testFindSettings(final Class<?> parameterType, final BeanInfo beanInfo, Class<?> propertyType, String accessorName, String mutatorName) {
        PropertyDescriptor settingsProperty = ReflectionUtilities.findSettingsProperty(parameterType, beanInfo);

        Assert.assertNotNull(settingsProperty);

        Assert.assertEquals(settingsProperty.getPropertyType(), propertyType);
        Assert.assertEquals(settingsProperty.getReadMethod().getName(), accessorName);
        Assert.assertEquals(settingsProperty.getWriteMethod().getName(), mutatorName);
    }

    @Test
    public void testCreateArgumentsArray() throws IntrospectionException {
        Object[] argArray = ReflectionUtilities.createArgumentsArray(
                new Class[]{String.class, Integer.class},
                new PerfectSettingsBean("Hello World", new Integer(42))
        );
        Assert.assertEquals(argArray[0], "Hello World");
        Assert.assertEquals(argArray[1], new Integer(42));
    }

    @Test
    public void testConstructionFromPerfectSettings()
            throws NoApplicableConstructorException, IntrospectionException, IllegalAccessException, ProcessorInstantiationException, InstantiationException, InvocationTargetException {

        Class<?> expectedClass = SubjectProcessor.class;
        Object settings = new PerfectSettingsBean("Hello World", new Integer(42));

        Object instance = ReflectionUtilities.createInstanceFromSettings(expectedClass, settings);
        Assert.assertNotNull(instance);
        Assert.assertTrue(expectedClass.isInstance(instance));

        SubjectProcessor sp = (SubjectProcessor)instance;
        Assert.assertEquals(sp.getIntProperty(), new Integer(42));
        Assert.assertEquals(sp.getStringProperty(), "Hello World");
    }

    @Test
    public void testConstructionFromStringSettings()
            throws NoApplicableConstructorException, IntrospectionException, IllegalAccessException, ProcessorInstantiationException, InstantiationException, InvocationTargetException {

        Class<?> expectedClass = SubjectProcessor.class;
        Object settings = new StringPropertySettingsBean("Whadever");

        Object instance = ReflectionUtilities.createInstanceFromSettings(expectedClass, settings);
        Assert.assertNotNull(instance);
        Assert.assertTrue(expectedClass.isInstance(instance));

        SubjectProcessor sp = (SubjectProcessor)instance;
        Assert.assertEquals(sp.getIntProperty(), null);
        Assert.assertEquals(sp.getStringProperty(), "Whadever");
    }

    /** Used as the subject of tests */
    static class PerfectSettingsBean {
        private String stringProperty;
        private Integer intProperty;

        public PerfectSettingsBean(final String stringProperty, final Integer intProperty) {
            this.stringProperty = stringProperty;
            this.intProperty = intProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(final String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public Integer getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(final Integer intProperty) {
            this.intProperty = intProperty;
        }
    }

    static class StringPropertySettingsBean {
        private String stringProperty;

        public StringPropertySettingsBean(final String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(final String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }

    static class SubjectProcessor {
        private final String stringProperty;
        private final Integer intProperty;

        public SubjectProcessor(String stringProperty) {
            this.stringProperty = stringProperty;
            this.intProperty = null;
        }

        public SubjectProcessor(Integer intProperty) {
            this.stringProperty = null;
            this.intProperty = intProperty;
        }

        public SubjectProcessor(String stringProperty, Integer intProperty) {
            this.stringProperty = stringProperty;
            this.intProperty = intProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public Integer getIntProperty() {
            return intProperty;
        }
    }
}
