package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.semantics.exceptions.UnknownModelClassException;

/**
 * The required grammar of a commandprovider factory method is similar to the
 * router method grammar with the addition of "create" and "Command"
 * added as a prefix and suffix respectively.
 * 
 */
public class CommandFactoryMethodSemanticsImpl
extends AbstractBaseSemanticsImpl implements CommandFactoryMethodSemantics {
    public static final String COMMAND_FACTORY_METHOD_PREFIX = "create";

    static String trimFactorySpecificParts(final String methodName) {
        String commandName = CommandClassSemanticsImpl.trimCommandSpecificParts(methodName);

        if (commandName.startsWith(COMMAND_FACTORY_METHOD_PREFIX)) {
            return commandName.substring(COMMAND_FACTORY_METHOD_PREFIX.length());
        } else {
            return commandName;
        }
    }

    CommandFactoryMethodSemanticsImpl(final ApplicationSemantics applicationSemantics,
                                      final String methodName)
	        throws CoreRouterSemanticsException
	{
        super(applicationSemantics, trimFactorySpecificParts(methodName));
	}

    CommandFactoryMethodSemanticsImpl(
            final ApplicationSemantics applicationSemantics,
            final VocabularyWord action,
            final String subject,
            final CollectionType collectionType,
            final VocabularyWord preposition,
            final String object)
            throws UnknownModelClassException {
        super(applicationSemantics, action, subject, collectionType, preposition, object);
    }

    CommandFactoryMethodSemanticsImpl(
            final ApplicationSemantics applicationSemantics,
            final AbstractBaseSemantics element)
    {
        super(applicationSemantics, element);
    }

    @Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
        sb.append(CommandClassSemanticsImpl.COMMAND_SUFFIX);

		if(sb.length() > 0)
			return COMMAND_FACTORY_METHOD_PREFIX + sb.toString();
		else
			return "";
	}


}
