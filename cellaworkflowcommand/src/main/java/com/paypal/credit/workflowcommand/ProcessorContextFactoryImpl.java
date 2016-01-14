package com.paypal.credit.workflowcommand;

import com.paypal.credit.core.commandprovider.exceptions.CommandInstantiationException;
import com.paypal.credit.workflow.RSProcessorContext;
import com.paypal.credit.workflow.exceptions.NoContextConstructorFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by cbeckey on 12/10/15.
 */
public class ProcessorContextFactoryImpl
        implements ProcessorContextFactory {

    /**
     *
     * @param contextClassName
     * @param parameters
     * @return
     * @throws CommandInstantiationException
     */
    @Override
    public RSProcessorContext createContext(final String contextClassName, final Object[] parameters) throws CommandInstantiationException {
        RSProcessorContext result = null;

        if (contextClassName != null && !contextClassName.isEmpty()) {
            try {
                Class<?>[] parameterTypes = Utility.getParameterTypes(parameters);
                Class<? extends RSProcessorContext> clazz = (Class<? extends RSProcessorContext>) Class.forName(contextClassName);

                if (parameters == null || parameters.length == 0) {
                    result = (RSProcessorContext) clazz.newInstance();
                } else {
                    for (Constructor<?> ctor : clazz.getConstructors()) {
                        if (isAssignableFrom(ctor.getParameterTypes(), parameterTypes)) {
                            result = (RSProcessorContext) ctor.newInstance(parameters);
                            break;
                        }
                    }
                    if (result == null) {
                        throw new NoContextConstructorFoundException(clazz, parameterTypes);
                    }
                }
            } catch (InvocationTargetException |
                    InstantiationException |
                    IllegalAccessException |
                    ClassNotFoundException |
                    ClassCastException |
                    NoContextConstructorFoundException x) {
                x.printStackTrace();
                throw new CommandInstantiationException("WorkflowCommandProvider", x);
            }

        } else {
            result = new RSProcessorContext();
        }

        return result;
    }

    /**
     * Determines if the Class in thisTypes are isAssignableFrom to the
     * corresponding types in thatTypes. In other words, iterates through
     * the arrays and calls thisTypes[index].isAssignableFrom(thatTypes[index]).
     * If any return false (from isAssignableFrom) then the entire result is false.
     * if both parameters are null or zero length arrays, will return true.
     * Arrays with a different length, will return false.
     *
     * @param thisTypes
     * @param thatTypes
     * @return
     */
    private boolean isAssignableFrom(final Class<?>[] thisTypes, final Class<?>[] thatTypes) {
        if (thisTypes == null && thatTypes == null) {
            return true;
        } else if (thisTypes == null && thatTypes != null) {
            return thatTypes.length == 0;
        } else if (thisTypes != null && thatTypes == null) {
            return thisTypes.length == 0;
        } else if (thisTypes.length != thatTypes.length) {
            return false;
        } else {
            for (int index = 0; index < thisTypes.length; ++index) {
                if (! thisTypes[index].isAssignableFrom(thatTypes[index])) {
                    return false;
                }
            }
            return true;
        }
    }
}
