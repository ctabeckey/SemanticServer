package com.paypal.credit.core.semantics;

import com.paypal.credit.core.commandprocessor.Command;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.semantics.exceptions.UnknownModelClassException;

import java.util.regex.Pattern;

/**
 * This classes defines the naming conventions for realizations of the
 * Command interface (i.e. router commandprovider).
 *
 */
public class CommandClassSemantics
extends AbstractBaseSemantics
{
    public static final String COMMAND_SUFFIX = "Command";

    static String trimCommandSpecificParts(final String commandName) {
        if (commandName.endsWith(COMMAND_SUFFIX)) {
            return commandName.substring(0, commandName.length()-COMMAND_SUFFIX.length());
        } else {
            return commandName;
        }
    }

    /**
     *
     * @param commandClassName
     * @throws CoreRouterSemanticsException
     */
    CommandClassSemantics(ApplicationSemantics applicationSemantics, String commandClassName)
            throws CoreRouterSemanticsException
    {
        super(applicationSemantics, trimCommandSpecificParts(commandClassName));
        if (!commandClassName.endsWith(COMMAND_SUFFIX)) {
            throw new CoreRouterSemanticsException(String.format("Command '%s' does not end with 'Command' or 'CommandImpl'", commandClassName));
        }
    }

    /**
     *
     * @param action
     * @param objectClassName
     * @param preposition
     * @param objectOfPreposition
     */
    CommandClassSemantics(
            final ApplicationSemantics applicationSemantics,
            final Action action,
            final String objectClassName,
            final CollectionType collectionType,
            final Preposition preposition,
            final String objectOfPreposition)
            throws UnknownModelClassException {
        super(applicationSemantics, action, objectClassName, collectionType, preposition, objectOfPreposition);
    }

    /**
     * @param applicationSemantics
     * @param source
     */
    CommandClassSemantics(final ApplicationSemantics applicationSemantics, final AbstractBaseSemantics source) {
        super(applicationSemantics, source);
    }

    /**
     *
     * @param commandClazz
     * @return
     */
    public boolean describes(final Class<? extends Command<?>> commandClazz) {
        String clazzSimpleName = commandClazz.getSimpleName();
        try {
            CommandClassSemantics clazzSemantics = getApplicationSemantics().createCommandClassSemantic(clazzSimpleName);
            return equals(clazzSemantics);
        } catch (CoreRouterSemanticsException e) {
            return false;
        }
    }

    /**
	 * return the name of the Command Class (e.g. getAuthorizationCommand)
	 * @return
     */
    @Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(COMMAND_SUFFIX);

		if(sb.length() > 0) {
            return sb.toString();
        } else {
            return "";
        }
	}

    /**
     * return the logical name of the Command (e.g. GetAuthorization)
     * @return
     */
    public String toBaseString()
    {
        return super.toString();
    }
}
