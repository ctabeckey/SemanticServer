package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.BeansType;
import com.paypal.credit.context.xml.ScopeType;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by cbeckey on 3/14/16.
 */
public class AbstractBeanInstanceFactoryTest {
    private ContextFactory ctxFactory;
    private Context ctx;

    @BeforeTest
    public void beforeTest() throws ContextInitializationException {
        ClassMatcher classMatcher = new ClassMatcher(GenericBeanInstance.class);

        ctxFactory = new ContextFactory();

        BeanType bean = new BeanType();
        bean.setId("id1");
        bean.setArtifact(null);
        bean.setClazz(GenericBeanInstance.class.getName());
        bean.setScope(ScopeType.PROTOTYPE);
        ctxFactory.with(bean);

        bean = new BeanType();
        bean.setId("id2");
        bean.setArtifact(null);
        bean.setClazz(GenericBeanInstance.class.getName());
        bean.setScope(ScopeType.PROTOTYPE);
        ctxFactory.with(bean);

        ctx = ctxFactory
                .build();
    }

    @Test
    public void testProperties() throws ContextInitializationException {
        BeanType beanType = new BeanType();
        beanType.setId("id");
        beanType.setArtifact(null);
        beanType.setClazz(GenericBeanInstance.class.getName());
        beanType.setScope(ScopeType.PROTOTYPE);

        AbstractBeanInstanceFactory ref = new MockAbstractBeanInstanceFactory(ctx, beanType);

        Assert.assertEquals(ref.getIdentifier(), "id");
        Assert.assertEquals(ref.getClazzName(), GenericBeanInstance.class.getName());
        Assert.assertEquals(ref.getContext(), ctx);
    }

    @Test
    public void testNegativeCircularDetection() throws ContextInitializationException {
        BeanType beanType = new BeanType();
        beanType.setId("id");
        beanType.setArtifact(null);
        beanType.setClazz(GenericBeanInstance.class.getName());
        beanType.setScope(ScopeType.PROTOTYPE);

        MockAbstractBeanInstanceFactory ref = new MockAbstractBeanInstanceFactory(ctx, beanType);
    }

    @Test
    public void testValidClassName() throws ContextInitializationException {
        BeanType beanType = new BeanType();
        beanType.setId("id");
        beanType.setArtifact(null);
        beanType.setClazz(GenericBeanInstance.class.getName());
        beanType.setScope(ScopeType.PROTOTYPE);

        AbstractBeanInstanceFactory ref = new MockAbstractBeanInstanceFactory(ctx, beanType);

        Assert.assertNotNull(ref);
        Assert.assertNotNull(ref.createBeanInstance());
    }

    @Test(expectedExceptions = {ContextInitializationException.class})
    public void testInvalidClassName() throws ContextInitializationException {
        BeanType beanType = new BeanType();
        beanType.setId("id");
        beanType.setArtifact(null);
        beanType.setClazz("com.junk.invalid.ClassName");
        beanType.setScope(ScopeType.PROTOTYPE);

        AbstractBeanInstanceFactory ref = new MockAbstractBeanInstanceFactory(ctx, beanType);

        ref.createBeanInstance();
    }

    private static class MockAbstractBeanInstanceFactory
            extends AbstractBeanInstanceFactory {

        public MockAbstractBeanInstanceFactory(Context context, BeanType beanType) throws ContextInitializationException {
            super(context, beanType.getId(), beanType.getArtifact(), beanType.getClazz(), null);
        }

        @Override
        public AbstractProperty morph(Class targetValueType) throws ContextInitializationException {
            return this;
        }

        @Override
        protected Context getContext() {
            return super.getContext();
        }

        /**
         * Get the value as the currently resolved type
         */
        @Override
        public Object getValue() throws ContextInitializationException {
            return createBeanInstance();
        }
    }

}
