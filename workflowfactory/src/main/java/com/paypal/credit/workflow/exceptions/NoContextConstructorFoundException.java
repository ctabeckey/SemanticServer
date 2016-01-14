package com.paypal.credit.workflow.exceptions;

import com.paypal.credit.workflow.RSProcessorContext;

/**
 * Created by cbeckey on 12/10/15.
 */
public class NoContextConstructorFoundException extends Exception {
    private static String createMessage(final Class<? extends RSProcessorContext> clazz, final Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Failed to find a suitable constructor %s(", clazz.getName()));

        StringBuilder sbParameterList = new StringBuilder("(");
        if (parameterTypes != null) {
            for (Class<?> parameterType : parameterTypes) {
                if (sbParameterList.length() > 1) {
                    sbParameterList.append(", ");
                }
                sbParameterList.append(parameterType == null ? "<null>" : parameterType.getName());
            }
        }
        sbParameterList.append(")");

        sb.append(sbParameterList);
        return sb.toString();
    }

    public NoContextConstructorFoundException(final Class<? extends RSProcessorContext> clazz, final Class<?>[] parameterTypes) {
        super(createMessage(clazz, parameterTypes));

    }
}
