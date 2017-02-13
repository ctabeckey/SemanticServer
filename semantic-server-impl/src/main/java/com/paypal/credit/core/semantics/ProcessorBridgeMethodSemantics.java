package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.semantics.exceptions.UnknownModelClassException;

import java.lang.reflect.Method;

/**
 * Router methods, except for methods pre-dating the semantic router, MUST follow the naming
 * convention prescribed below.
 * Methods are named as follows:
 * action + object + optional(preposition + related_object)
 * - action describes what is to be done with the object
 * - object describes the object type being acted on
 * - the optional preposition and object describe the relationship of additional objects to the actio
 *   being taken
 * - the optional preposition and related_object are only required when it is necessary to differentiate
 *   between commandprovider with similar action/object pairs
 * - method names use camel-casing, where the first char of each word is capitalized, except that the
 *   first character of the first word is always lower-case
 * Neither the action name, the object name, nor the related_object name may include any of the words
 * reserved for the action or the preposition.
 * examples: getStudyList(), getStudyListByStudyFilter()
 * The action MUST be one of POST, GET, PUT, DELETE, READ, UPDATE
 * The object MUST be the simple name of a business object (a collection type may be concatenated)
 * The preposition MUST be one of BY, LIKE
 * The related_object MUST be either the simple name of a business object or the simple name of a core Java class
 * 
 * Router methods that pre-date the semantic router are mapped using a hard-coded mapping to 
 * the "correct" names outside of this package.
 * 
 */
public class ProcessorBridgeMethodSemantics
extends AbstractBaseSemantics
{
	ProcessorBridgeMethodSemantics(
			final ApplicationSemantics applicationSemantics,
			final Method method)
			throws CoreRouterSemanticsException
	{
		super(applicationSemantics, method.getName());
	}

	ProcessorBridgeMethodSemantics(
            final ApplicationSemantics applicationSemantics,
            final String elementName)
	        throws CoreRouterSemanticsException
	{
        super(applicationSemantics, elementName);
	}
	
	ProcessorBridgeMethodSemantics(
            final ApplicationSemantics applicationSemantics,
            final AbstractBaseSemantics coreRouterElement)
            throws CoreRouterSemanticsException
	{
		super(applicationSemantics, coreRouterElement);
	}

    ProcessorBridgeMethodSemantics(
            ApplicationSemantics applicationSemantics,
            Action action,
            String subject,
            CollectionType collectionType,
            Preposition preposition,
            String object)
            throws UnknownModelClassException {
        super(applicationSemantics, action, subject, collectionType, preposition, object);
    }

    @Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		String rawName = sb.toString();

        return SemanticsUtility.setFirstCharCase(rawName, false);
	}

}
