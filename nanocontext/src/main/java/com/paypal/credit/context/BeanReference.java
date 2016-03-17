package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidMorphTargetException;

/**
 * A placeholder that is stuffed into the context when a ref element is encountered.
 * This instance will be replaced on the first use of the bean with a reference to
 * the actual instance.
 */
final class BeanReference<T> extends AbstractProperty<T> {
    /** */
    final private String referencedBeanId;

    private final Class<T> valueType;

    /**
     * @param context
     * @param referencedBeanId
     */
    protected BeanReference(final Context context, final String referencedBeanId)
            throws ContextInitializationException {
        super(context);
        this.referencedBeanId = referencedBeanId;
        this.valueType = null;      // delegate to the referenced bean
    }

    /**
     *
     * @param context
     * @param referencedBeanId
     * @param valueType
     * @throws ContextInitializationException
     */
    private BeanReference(final Context context, final String referencedBeanId, final Class<T> valueType)
            throws ContextInitializationException {
        super(context);
        this.referencedBeanId = referencedBeanId;
        this.valueType = valueType;     // override the referenced beans type
    }

    /**
     *
     * @return
     */
    public String getReferencedBeanIdentifier() {
        return this.referencedBeanId;
    }

    /**
     *
     * @return
     * @throws ContextInitializationException
     */
    private AbstractReferencableProperty getReferencedBean()
            throws ContextInitializationException {
        AbstractProperty beanRef = getContext().getBeanReference(getReferencedBeanIdentifier());

        return beanRef instanceof AbstractReferencableProperty ?
                (AbstractReferencableProperty)beanRef : null;
    }
    /**
     * Get the value from the referenced bean, morphing types if needed.
     * @return the bean value
     */
    public T getValue() throws ContextInitializationException {
        AbstractReferencableProperty beanRef = getReferencedBean();
        if (beanRef != null) {
            if (this.valueType != null) {
                return (T)beanRef.getValue(this.valueType);
            }
            return (T) beanRef.getValue();
        }
        return null;
    }

    /**
     * Get the value as the given type.
     * This method should do conversion, valueOf, instantiation, etc as it needs to.
     *
     * @param targetClazz the target type
     * @return an instance of the constant value as the given type
     * @throws ContextInitializationException - usually if the conversion cannot be done
     * @see #isResolvableAs(Class)
     */
    @Override
    public <S> S getValue(Class<S> targetClazz) throws ContextInitializationException {
        if (isResolvableAs(targetClazz)) {
            return targetClazz.cast(getValue());
        } else {
            throw new InvalidMorphTargetException(this, getValueType(), targetClazz);
        }
    }

    /**
     *
     * @return
     * @throws BeanClassNotFoundException
     */
    public Class<T> getValueType() throws ContextInitializationException {
        // if the valueType has not been set explicitly then delegate to the referenced bean
        if (this.valueType == null) {
            AbstractReferencableProperty beanRef = getReferencedBean();
            return (Class<T>) (beanRef == null ? null : beanRef.getValueType());
        } else {
            return this.valueType;
        }

    }

    /**
     * Returns true if the property can be resolved as the given type
     * Always delegate to the referenced bean.
     * @param clazz
     */
    @Override
    public boolean isResolvableAs(Class<?> clazz) throws ContextInitializationException {
        AbstractReferencableProperty beanRef = getReferencedBean();
        return beanRef == null ? false : beanRef.isResolvableAs(clazz);
    }

}
