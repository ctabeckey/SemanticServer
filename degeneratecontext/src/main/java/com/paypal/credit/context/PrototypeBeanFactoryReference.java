package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.FailedToInstantiateBeanException;
import com.paypal.credit.context.exceptions.NoApplicableConstructorException;
import com.paypal.credit.context.exceptions.SparseArgumentListDetectedException;
import com.paypal.credit.context.xml.BeanType;
import com.paypal.credit.context.xml.ConstructorArgType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by cbeckey on 2/8/16.
 */
public class PrototypeBeanFactoryReference<T> extends BeanFactoryReference<T> {

    /**
     *
     * @param beanReferenceFactory
     * @param beanType
     * @throws ContextInitializationException
     */
    protected PrototypeBeanFactoryReference(final BeanReferenceFactory beanReferenceFactory, final BeanType beanType) throws ContextInitializationException {
        super(beanReferenceFactory, beanType);
    }

    /**
     *
     * @param ctx
     * @param clazz
     * @return
     * @throws ContextInitializationException
     */
    @Override
    T getBeanInstance(final Context ctx, final Class clazz) throws ContextInitializationException {
        T instance = createBeanInstance();
        return instance;
    }
}
