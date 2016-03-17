package com.paypal.credit.context.exceptions;

import com.paypal.credit.context.AbstractProperty;
import com.paypal.credit.context.xml.ConstructorArgType;

import java.util.List;

/**
 * Created by cbeckey on 2/4/16.
 */
public class NoApplicableConstructorException extends ContextInitializationException {

    private static final String createMessage(final Class<?> beanClazz, final List<AbstractProperty> properties) {
        StringBuilder parameterDescriptions = new StringBuilder();

        if (properties != null) {
            for (AbstractProperty argType : properties) {
                if (parameterDescriptions.length() > 0) {
                    parameterDescriptions.append(',');
                }

                parameterDescriptions.append(argType.toString());
            }
        }

        return String.format("Unable to find suitable constructor in class %s (%s)",
                beanClazz == null ? "" : beanClazz.getName(),
                parameterDescriptions.toString()
        );
    }

    public NoApplicableConstructorException(final Class<?> beanClazz, final List<AbstractProperty> properties) {
        super(createMessage(beanClazz, properties));
    }
}
