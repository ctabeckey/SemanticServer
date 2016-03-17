package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidMorphTargetException;

/**
 * The degenerate case of an AbstractBeanReference, where the bean is extant and its
 * reference is known.
 */
public final class PreresolvedBean<T>
        extends AbstractReferencableProperty<T> {
    /** A reference to the bean itself */
    private final T bean;

    /** The type of the bean as the outside world sees it */
    private final Class<?> resolvedType;

    /**
     * The "external" constructor.
     *
     * @param ctx
     * @param identifier
     * @param bean
     * @throws ContextInitializationException
     */
    PreresolvedBean(final Context ctx, final String identifier, final T bean)
            throws ContextInitializationException {
        this(ctx, identifier, bean, bean.getClass());
    }

    /**
     * The "internal" constructor, used only for morphing operation.
     *
     * @param ctx
     * @param bean
     * @param resolvedType
     * @throws ContextInitializationException
     */
    private PreresolvedBean(final Context ctx, final String identifier, final T bean, final Class<?> resolvedType)
            throws ContextInitializationException {
        super(ctx, identifier);
        this.bean = bean;
        this.resolvedType = resolvedType;
    }

    /**
     * @return
     */
    @Override
    public T getValue() throws ContextInitializationException {
        return bean;
    }

    /**
     * Only works if the target class is a superclass result of a getValueType() call.
     *
     * @param targetClazz the target type
     * @param <S>
     * @return a cast of the getValue() result
     * @throws ContextInitializationException - if the target type is not the type or a super-type
     */
    @Override
    public <S> S getValue(final Class<S> targetClazz)
            throws ContextInitializationException {
        if (isResolvableAs(targetClazz)) {
            return targetClazz.cast(getValue());
        } else {
            throw new InvalidMorphTargetException(this, getValueType(), targetClazz);
        }
    }

    /**
     * Returns the currently resolved type of the property
     */
    @Override
    public Class<?> getValueType() throws ContextInitializationException {
        return this.resolvedType;
    }

    /**
     * Returns true if the property can be resolved as the given type
     *
     * @param clazz
     */
    @Override
    public boolean isResolvableAs(Class<?> clazz) throws ContextInitializationException {
        return clazz.isAssignableFrom(getValueType());
    }

    @Override
    public String toString() {
        return "PreresolvedBeanReference{" +
                "bean=" + bean.toString() +
                ", resolvedType=" + resolvedType.toString() +
                '}';
    }
}
