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
     * Return an instance of the same class where the target resolution type is the
     * given type.
     * Derivations of this method SHOULD return <code>this</code> if the targetValueType is
     * exactly the same as the result of getValueType().
     *
     * @see #isResolvableAs(Class) may be called to determine whether the morph will be
     * successful before calling this method
     *
     * @param targetValueType
     * @param <S>
     * @return
     */
    public <S> AbstractProperty<S> morph(Class<S> targetValueType)
            throws ContextInitializationException {
        if (targetValueType != null && targetValueType.equals(getValueType())) {
            return (AbstractProperty<S>) this;
        }
        throw new AbstractPropertyClassDoesNotProperlyImplementMorph(this.getClass());
    }
}
