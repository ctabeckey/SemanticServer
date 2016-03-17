package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.ContextInitializationException;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> PreresolvedBean<S> morph(Class<S> targetValueType) throws ContextInitializationException {
        if (this.getValueType().equals(targetValueType)) {
            return (PreresolvedBean<S>) this;
        }

        isResolvableAs(targetValueType);

        return new PreresolvedBean(this.getContext(), this.getIdentifier(), this.bean, targetValueType);
    }

    @Override
    public String toString() {
        return "PreresolvedBeanReference{" +
                "bean=" + bean.toString() +
                ", resolvedType=" + resolvedType.toString() +
                '}';
    }
}
