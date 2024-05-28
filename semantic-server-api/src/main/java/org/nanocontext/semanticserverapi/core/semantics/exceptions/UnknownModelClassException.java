package org.nanocontext.semanticserverapi.core.semantics.exceptions;

import org.nanocontext.semanticserverapi.core.semantics.ModelVocabulary;

import java.util.Locale;

/**
 * An exception that is thrown when it is determined that a given class
 * or class identifier is not part of the defined model (as determined by
 * the ObjectVocabulary.
 */
public class UnknownModelClassException
extends CoreRouterSemanticsException {
    private static String createMessage(final Locale locale, final ModelVocabulary vocabulary, final String classSimpleName) {
        return String.format(locale, "Object tye %s is not a component of the object model contained in %s", classSimpleName, vocabulary.getClass().getName());
    }

    /**
     *
     * @param vocabulary
     * @param classSimpleName
     */
    public UnknownModelClassException(final ModelVocabulary vocabulary, final String classSimpleName) {
        super(createMessage(Locale.getDefault(), vocabulary, classSimpleName));
    }
}
