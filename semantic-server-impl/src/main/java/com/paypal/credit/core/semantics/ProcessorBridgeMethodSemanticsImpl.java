package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.credit.core.semantics.exceptions.UnknownModelClassException;

import java.lang.reflect.Method;

/**
 * Router methods MUST follow the naming convention prescribed below.
 * Methods are named as follows:
 * action + subject + optional(preposition + object)
 * - action describes what is to be done with the object
 * - subject describes the subject type being acted on
 * - the optional preposition and object describe the relationship of additional objects to the action
 *   being taken
 * - the optional preposition and object are only required when it is necessary to differentiate
 *   between commands with similar action/object pairs
 * - method names use camel-casing, where the first char of each word is capitalized, except that the
 *   first character of the first word is always lower-case
 * Neither the action name, the subject name, nor the object name may include any of the words
 * reserved for the action or the preposition.
 * examples: getStudyList(), getStudyListByStudyFilter()
 * The action is usually one of POST, GET, PUT, DELETE, READ, UPDATE
 * The subject MUST be the simple class name of a business object (a collection type may be concatenated)
 * The preposition is usually one of BY, LIKE
 * The object MUST be either the simple name of a business object or the simple name of a core Java class
 */
public class ProcessorBridgeMethodSemanticsImpl
		extends AbstractBaseSemanticsImpl
		implements ProcessorBridgeMethodSemantics {
	ProcessorBridgeMethodSemanticsImpl(
			final ApplicationSemantics applicationSemantics,
			final Method method)
			throws CoreRouterSemanticsException
	{
		super(applicationSemantics, method.getName());
	}

	ProcessorBridgeMethodSemanticsImpl(
            final ApplicationSemantics applicationSemantics,
            final String elementName)
	        throws CoreRouterSemanticsException
	{
        super(applicationSemantics, elementName);
	}
	
	ProcessorBridgeMethodSemanticsImpl(
            final ApplicationSemantics applicationSemantics,
            final AbstractBaseSemantics coreRouterElement)
            throws CoreRouterSemanticsException
	{
		super(applicationSemantics, coreRouterElement);
	}

    ProcessorBridgeMethodSemanticsImpl(
            ApplicationSemantics applicationSemantics,
			VocabularyWord action,
            String subject,
            CollectionType collectionType,
			VocabularyWord preposition,
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
