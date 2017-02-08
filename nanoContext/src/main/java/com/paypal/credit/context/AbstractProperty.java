package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.AbstractPropertyClassDoesNotProperlyImplementMorph;
import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.ContextInitializationException;

/**
 * A Property may be a constant value, a bean, a list or a reference to another bean.
 * A property has a value.
 * The type of the value is always "resolved" to a particular type. For beans and bean
 * references the type is always the type from the context configuration (XML file).
 * For constants, the type is a String when the property is first created. As part of
 * the process of matching a property to concrete constructor arguments, a property may
 * be "morphed" to another resolved type. An AbstractProperty and all of its derivations
 * is immutable, so a morph actually creates another instance.
 * A List may also be morphed, its type is dependent on its element types and its usage
 * as either a List or an array.
 */
public abstract class AbstractProperty<T> {
    private final Context context;

    /**
     *
     * @param context
     */
    AbstractProperty(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("<ctor> AbstractProperty(context), context must not be null");
        }
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    /** Get the value as the currently resolved type */
    public abstract T getValue() throws ContextInitializationException;

    /** Returns the currently resolved type of the property */
    public abstract Class<?> getValueType() throws ContextInitializationException;

    /** Returns true if the property can be resolved as the given type */
    public abstract boolean isResolvableAs(final Class<?> clazz) throws ContextInitializationException;

    /**
     * Get the value as the given type.
     * This method should do conversion, valueOf, instantiation, etc as it needs to.
     *
     * @see #isResolvableAs(Class)
     *
     * @param targetClazz the target type
     * @param <S> the Type of the target type
     * @return an instance of the constant value as the given type
     * @throws ContextInitializationException - usually if the conversion cannot be done
     */
    public abstract <S> S getValue(final Class<S> targetClazz)
            throws ContextInitializationException;
}
