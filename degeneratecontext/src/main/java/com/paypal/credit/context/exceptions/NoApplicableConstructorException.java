package com.paypal.credit.context.exceptions;

import com.paypal.credit.context.xml.ConstructorArgType;

import java.util.List;

/**
 * Created by cbeckey on 2/4/16.
 */
public class NoApplicableConstructorException extends ContextInitializationException {
    private static final String createMessage(final Class<?> beanClazz, final List<ConstructorArgType> orderedParameters) {
        StringBuilder parameterDescriptions = new StringBuilder();

        if (orderedParameters != null) {
            for (ConstructorArgType argType : orderedParameters) {
                if (parameterDescriptions.length() > 0) {
                    parameterDescriptions.append(',');
                }
                if (argType.getBean() != null) {
                    parameterDescriptions.append(
                            String.format("%s{%s}", argType.getBean().getId(), argType.getBean().getClazz())
                    );
                } else if (argType.getValue() != null) {
                    parameterDescriptions.append(
                            String.format("\"%s\"", argType.getValue().toString())
                    );

                } else if (argType.getList() != null) {
                    parameterDescriptions.append("list ...");
                }
            }
        }

        return String.format("Unable to find suitable constructor in class %s (%s)",
                beanClazz == null ? "" : beanClazz.getName(),
                parameterDescriptions.toString()
        );
    }

    public NoApplicableConstructorException(final Class<?> beanClazz, final List<ConstructorArgType> orderedParameters) {
        super(createMessage(beanClazz, orderedParameters));
    }
}
