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
                return (T)beanRef.morph(this.valueType).getValue();
            }
            return (T) beanRef.getValue();
        }
        return null;
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

    /**
     * Return an instance of the same class where the target resolution type is the
     * given type.
     * Derivations of this method MUST return <code>this</code> if the targetValueType is
     * exactly the same as the result of getValueType().
     *
     * @param targetValueType
     * @return
     * @see #isResolvableAs(Class) may be called to determine whether the morph will be
     * successful before calling this method.
     */
    @Override
    public <S> AbstractProperty<S> morph(Class<S> targetValueType) throws ContextInitializationException {
        if (getReferencedBean().isResolvableAs(targetValueType)) {
            return new BeanReference<>(getContext(), getReferencedBeanIdentifier(), targetValueType);
        } else {
            throw new InvalidMorphTargetException(this, getReferencedBean().getValueType(), targetValueType);
        }
    }
}
