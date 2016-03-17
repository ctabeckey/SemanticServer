package com.paypal.credit.context;

import com.paypal.credit.context.exceptions.BeanClassNotFoundException;
import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.context.exceptions.InvalidMorphTargetException;
import com.paypal.credit.context.exceptions.UnknownCollectionTypeException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by cbeckey on 3/15/16.
 */
public class ListProperty<C> extends AbstractProperty<C> {
    private final List<AbstractProperty> properties;
    private final Class<C> valueType;       // this will be either List or an array
    private final Class<?> elementType;

    public ListProperty(final Context context, final List<AbstractProperty> properties) {
        super(context);
        this.properties = properties;
        this.valueType = (Class<C>) List.class;
        this.elementType = Object.class;
    }

    private ListProperty(final Context context, final List<AbstractProperty> properties, Class<C> valueType, Class<?> elementType) {
        super(context);
        this.properties = properties;
        this.valueType = (Class<C>) valueType;
        this.elementType = elementType;
    }

    /** */
    @Override
    public C getValue() throws ContextInitializationException {
        if (List.class.equals(valueType)) {
            List value = new ArrayList<>(properties.size());

            for (AbstractProperty property : properties) {
                value.add(property.getValue(getElementType()));
            }

            return (C) value;
        } else if (valueType.isArray()) {
            Object value = Array.newInstance(elementType, properties.size());
            int index = 0;
            for (AbstractProperty property : properties) {
                Array.set(value, index++, property.getValue(getElementType()));
            }

            return (C) value;
        } else {
            return null;
        }

    }

    /**
     * Get the List as an instance of the target type.
     * It is strongly suggested that the List specific
     * @see #getValue(Class, Class) be used if the target class is
     * a collection.
     * This method is reliable if the targetClazz is an instance of an array.
     * The element types of Collection types are not reliably determinable at runtime.
     *
     * @param targetClazz the target type
     * @param <S> the target class Type
     * @return the list as a targetClazz type
     * @throws ContextInitializationException - if the target type is not the type or a super-type
     */
    @Override
    public <S> S getValue(final Class<S> targetClazz)
            throws ContextInitializationException {
        if (isResolvableAs(targetClazz)) {
            Class<?> targetElementClazz = InstantiationUtility.extractElementType(targetClazz);
            return getValue(targetClazz, targetElementClazz);
        } else {
            throw new InvalidMorphTargetException(this, getValueType(), targetClazz);
        }
    }

    /**
     *
     * @param targetClazz the class of the target type
     * @param targetElementClazz the class of the target element type
     * @param <S> the target class Type
     * @param <E> the target element Type
     * @return the list as a targetClazz type
     * @throws ContextInitializationException - if the target type is not the type or a super-type
     */
    public <S, E> S getValue(final Class<S> targetClazz, final Class<E> targetElementClazz)
            throws ContextInitializationException {
        // assure that the target element class is not null
        Class<?> fixedTargetElementClazz = targetElementClazz == null ?
                getElementType() : targetElementClazz;

        if (List.class.equals(targetClazz)) {
            List value = new ArrayList<>(properties.size());

            for (AbstractProperty property : properties) {
                value.add(property.getValue(fixedTargetElementClazz));
            }

            return (S) value;
        } else if (targetClazz.isArray()) {
            Object value = Array.newInstance(targetElementClazz, properties.size());
            int index = 0;
            for (AbstractProperty property : properties) {
                Array.set(value, index++, property.getValue(fixedTargetElementClazz));
            }

            return (S) value;
        } else {
            throw new InvalidMorphTargetException(this, getValueType(), targetClazz);
        }

    }

    /** */
    @Override
    public Class<?> getValueType() throws BeanClassNotFoundException {
        return List.class;
    }

    private Class<?> getElementType() throws BeanClassNotFoundException {
        return this.elementType;
    }

    /**
     * Return true if the ListProperty value is resolvable as the given class.
     *
     * @param clazz
     * @return
     * @throws BeanClassNotFoundException
     */
    public boolean isResolvableAs(final Class<?> clazz)
            throws ContextInitializationException {

        // if the target Class is an array or a Collection (Set or List) and the
        // properties can be resolved as the element type
        if (Collection.class.isAssignableFrom(clazz)) {
            // can't reliably get the element type
            // hope for the best
            return true;

        } else if (clazz.isArray() && !clazz.getComponentType().isArray()) {
            Class<?> elementClazz = clazz.getComponentType();
            return isResolvableAsElementType(elementClazz);

        } else {
            return false;
        }
    }

    /**
     *
     * @param elementType
     * @return
     * @throws ContextInitializationException
     */
    public boolean isResolvableAsElementType(final Class<?> elementType)
            throws ContextInitializationException {

        for (AbstractProperty property : this.properties) {
            if (! property.isResolvableAs(elementType)) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param targetElementType
     * @return
     * @throws ContextInitializationException
     */
    public ListProperty morph(Class<?> valueType, Class<?> targetElementType) throws ContextInitializationException {
        if (this.isResolvableAsElementType(targetElementType)) {
            return new ListProperty(getContext(), this.properties, valueType, targetElementType);
        }
        throw new UnknownCollectionTypeException(targetElementType);
    }

    @Override
    public String toString() {
        return "ListProperty{" +
                "length=" + properties.size() +
                '}';
    }
}
