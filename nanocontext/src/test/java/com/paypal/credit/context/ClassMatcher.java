package com.paypal.credit.context;

import org.mockito.ArgumentMatcher;

/**
 * Created by cbeckey on 3/14/16.
 */
public class ClassMatcher implements ArgumentMatcher<Class<?>> {
    private final Class<?> clazz;

    public ClassMatcher(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean matches(Object o) {
        return this.clazz.equals(o);
    }
}
