package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.CannotCreateObjectFromStringException;
import com.paypal.credit.context.exceptions.ContextInitializationException;

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

    /** */
    @Override
    public Class<T> getValueType() {
        return this.valueType;
    }

    /**
     * Because of the order of execution, all constants are created as String values.
     * When the constructor of a Bean is called, the type may be morphed to make a compatible value
     * with a constructor argument.
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
    public ConstantProperty morph(Class targetValueType) throws ContextInitializationException {
        // return this if the type is the same
        if (this.getValueType().equals(targetValueType)) {
            return (ConstantProperty<T>) this;
        }

        // this call will generate an exception if the morph cannot succeed.
        Object value = InstantiationUtility.createInstanceFromStringValue(targetValueType, this.rawValue, true);
        return new ConstantProperty(getContext(), this.rawValue, targetValueType, value);
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
