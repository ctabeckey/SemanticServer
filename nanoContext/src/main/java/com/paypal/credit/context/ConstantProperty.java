package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidMorphTargetException;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cbeckey on 3/15/16.
 */
public class ConstantProperty<T> extends AbstractProperty<T> {
    private final String rawValue;
    private final Class<T> valueType;
    private ReentrantLock instantiationLock = new ReentrantLock();
    private T value;

    /**
     *
     * @param rawValue
     * @param valueType
     */
    ConstantProperty(final Context context, final String rawValue, final Class<T> valueType) {
        super(context);
        this.rawValue = rawValue;
        this.valueType = valueType;
    }

    private ConstantProperty(final Context context, final String rawValue, final Class<T> valueType, T value) {
        super(context);
        this.rawValue = rawValue;
        this.valueType = valueType;
        this.value = value;
    }

    /** */
    @Override
    public T getValue() throws CannotCreateObjectFromStringException {
        instantiationLock.lock();
        try {
            if (value == null) {
                value = InstantiationUtility.createInstanceFromStringValue(this.valueType, this.rawValue, true);
            }
        } finally {
            instantiationLock.unlock();
        }

        return value;
    }

    /**
     * Get the value as the given type.
     * This method will do conversion, valueOf, instantiation, etc as it needs to.
     * NOTE: unlike isResolvableAs(), which is restricted to primitive and java.lang.*
     * classes, this method will try to convert to whatever class is given. Exceptions
     * may be thrown if the conversion cannot be accomplished. In practice the target class
     * must have a static valueOf(String) method or a constructor expecting a single String
     * parameter.
     *
     * @see #isResolvableAs(Class)
     *
     * @param targetClazz the target type
     * @param <S> the Type of the target type
     * @return an instance of the constant value as the given type
     * @throws ContextInitializationException - usually if the conversion cannot be done
     */
    public <S> S getValue(final Class<S> targetClazz)
            throws ContextInitializationException {
        if (targetClazz == null || targetClazz.equals(getValueType())) {
            return (S)getValue();
        } else {
            if (isResolvableAs(targetClazz)) {
                return InstantiationUtility.createInstanceFromStringValue(targetClazz, this.rawValue, false);
            }
            throw new InvalidMorphTargetException(this, getValueType(), targetClazz);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getValueType() {
        return this.valueType;
    }

    /**
     * Because of the order of execution, all constants are created as String values.
     * When the constructor of a Bean is called, the type may be morphed to make a compatible value
     * with a constructor argument.
     *
     * NOTE: the targetValueType must be either a primitive, Class, or a class that is part of
     * the java.lang package. This class explicitly restricts its getValue() result to one
     * of those types.
     *
     * @param targetValueType
     * @return
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResolvableAs(Class<?> targetValueType) throws ContextInitializationException {
        try {
            InstantiationUtility.createInstanceFromStringValue(targetValueType, this.rawValue, true);
            return true;
        } catch(CannotCreateObjectFromStringException ccofsX) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ConstantProperty{" +
                "valueType=" + valueType.toString() +
                ", rawValue='" + rawValue.toString() + '\'' +
                '}';
    }
}
