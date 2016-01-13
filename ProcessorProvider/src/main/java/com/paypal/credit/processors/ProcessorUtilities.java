package com.paypal.credit.processors;

import com.paypal.credit.processors.exceptions.InvalidProcessorException;
import com.paypal.credit.workflow.RSProcessor;
import com.paypal.credit.workflow.RSProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of utilities useful for Processor Providers
 */
public final class ProcessorUtilities {
    /** The class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorUtilities.class);

    // The following fields are used to both validate that the process() method
    // exists as it did when this code was written and also to find process()
    // methods in RSProcessor derivations at runtime.
    /** The name of the process method declared in the RSProcessor interface. */
    public static final String PROCESS_METHOD_NAME = "process";
    /** The parameter type(s) of the process method declared in the RSProcessor interface. */
    public final static Class<?> PROCESSOR_CONTEXT_TYPE = RSProcessorContext.class;
    /** The parameter type(s) of the process method declared in the RSProcessor interface. */
    public final static Class<?>[] PROCESS_METHOD_PARAMETER_TYPES = new Class<?>[]{PROCESSOR_CONTEXT_TYPE};

    /**
     * As the ProcessorProvider relies on finding the process() method, check that the
     * definition of RSProcessor has not changed in a way that would break ProcessorProvider.
     */
    static {
        try {
            RSProcessor.class.getMethod(PROCESS_METHOD_NAME, PROCESS_METHOD_PARAMETER_TYPES);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(
                    String.format("%s no longer defines method %s(%s), code has changed in a way that breaks ProcessorProvider",
                            RSProcessor.class.getName(), PROCESS_METHOD_NAME, PROCESS_METHOD_PARAMETER_TYPES[0].getName()
                    )
            );
        }
    }

    /** Declare a private constructor to prevent instantiation */
    private ProcessorUtilities() { }

    /**
     * Given a name and an array of Class instances, return a JSON formatted String
     * of the name as the key and the class names as a JSON array
     * @param name
     * @param groups
     * @return
     */
    public static final String createValidationGroupDescription(String name, Class<?>[] groups) {
        StringBuilder sb = new StringBuilder();
        if (groups != null) {
            for (Class<?> group : groups) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("'" + group.getName() + "'");
            }
        }
        return name + "[" + sb.toString() + "]";
    }

    /**
     * Validate that a Processor is, indeed, a valid processor class and return the process() method.
     * To be valid it must:
     * 1.) implement RSProcessor
     * 2.) have exactly one process() method which overrides boolean RSProcessor.process(RSProcessorContext):
     * 3.)
     * @param clazz
     * @throws InvalidProcessorException
     */
    public static final Method findProcessMethod(Class<?> clazz)
            throws InvalidProcessorException {
        if ( !RSProcessor.class.isAssignableFrom(clazz)) {
            throw new InvalidProcessorException(clazz);
        }

        Set<Method> processMethods = new HashSet<>();

        for( Method method : clazz.getMethods()) {
            if (PROCESS_METHOD_NAME.equals(method.getName())) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1
                        && RSProcessorContext.class.isAssignableFrom(parameters[0])
                        && boolean.class == method.getReturnType()) {
                    processMethods.add(method);
                }
            }
        }

        // interesting case with generics, the RSProcessor class may have two
        // process() methods, one with its declared type and one with the base
        // type as declared by the RSProcessor interface declaration (i.e. RSProcessorContext)
        // We want the type specific override if there are two versions, remove
        // the base class declared method.
        if (processMethods.size() == 2) {
            Method baseClassTypeMethod = null;

            for (Method method : processMethods) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1
                        && RSProcessorContext.class.equals(parameters[0])
                        && boolean.class == method.getReturnType()) {
                    baseClassTypeMethod = method;
                    break;
                }
            }
            if (baseClassTypeMethod != null) {
                processMethods.remove(baseClassTypeMethod);
            }
        }

        if (processMethods.size() != 1) {
            throw new InvalidProcessorException(clazz);
        }

        return processMethods.iterator().next();
    }

}
