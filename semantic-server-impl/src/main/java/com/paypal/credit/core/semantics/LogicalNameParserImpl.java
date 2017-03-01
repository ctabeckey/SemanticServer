package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;

/**
 * Parses the logical name into Action, Subject (CollectionType)(, Preposition, and Object)
 * This DOES NOT validate the Subject or Object against the application model, that is the
 * responsibility of the AbstractBaseSemantics class.
 * Note that names like "GetThingCollection" will return a Subject of "ThingCollection",
 * the end of the Subject is either the start of a CollectionType, Preposition or the end of
 * the String.
 */
class LogicalNameParserImpl implements LogicalNameParser {
    private final Vocabulary actionVocabulary;
    private final ModelVocabulary subjectVocabulary;
    private final Vocabulary prepositionVocabulary;
    private final ModelVocabulary objectVocabulary;

    public LogicalNameParserImpl(
            @NotNull final Vocabulary actionVocabulary,
            @NotNull final ModelVocabulary subjectVocabulary,
            @NotNull final Vocabulary prepositionVocabulary,
            @NotNull final ModelVocabulary objectVocabulary) {
        this.actionVocabulary = actionVocabulary;
        this.objectVocabulary = objectVocabulary;
        this.prepositionVocabulary = prepositionVocabulary;
        this.subjectVocabulary = subjectVocabulary;
    }

    /**
     * Parse a String like this:
     * ActionSubject[Collection][PrepositionObject]
     * into Action, Subject, Collection, Preposition and Object
     *
     * @param logicalName
     * @throws CoreRouterSemanticsException
     */
    @Override
    public ParsedName parse(final String logicalName)
            throws CoreRouterSemanticsException {
        VocabularyWord action;
        String subject;
        CollectionType collectionType;
        VocabularyWord preposition;
        String object;


        // extract the Action verb, which must be at the start
        Matcher actionMatcher = this.actionVocabulary.getAnyPattern().matcher(logicalName);
        if (actionMatcher.find() && actionMatcher.start() == 0) {
            String actionIdentifier = logicalName.substring(actionMatcher.start(), actionMatcher.end());
            action = this.actionVocabulary.find(actionIdentifier);

            int endOfSubject = -1;
            Matcher collectionTypeMatcher = CollectionType.getAnyPattern().matcher(logicalName);
            if (collectionTypeMatcher.find(actionMatcher.end())) {
                String collectionIdentifier = logicalName.substring(collectionTypeMatcher.start(), collectionTypeMatcher.end());
                endOfSubject = collectionTypeMatcher.start();
                collectionType = CollectionType.find(collectionIdentifier);
                if (collectionType == null) {
                    throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s includes an unknown collection type.", logicalName));
                }
            } else {
                collectionType = null;
            }

            Matcher prepositionMatcher = this.prepositionVocabulary.getAnyPattern().matcher(logicalName);
            if (prepositionMatcher.find(Math.max(actionMatcher.end(), endOfSubject))) {
                String prepositionIdentifier = logicalName.substring(prepositionMatcher.start(), prepositionMatcher.end());
                if (endOfSubject == -1) {
                    endOfSubject = prepositionMatcher.start();
                }
                preposition = this.prepositionVocabulary.find(prepositionIdentifier);
                if (preposition == null) {
                    throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s includes an unknown preposition.", logicalName));
                }

                object = logicalName.substring(prepositionMatcher.end());
                if (object == null || object.isEmpty()) {
                    throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s included a preposition but does not include an object.", logicalName));
                }
            } else {
                preposition = null;
                object = null;
            }

            if (endOfSubject == -1) {
                endOfSubject = logicalName.length();
            }

            subject = logicalName.substring(actionMatcher.end(), endOfSubject);
            if (subject == null || subject.isEmpty()) {
                throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s does not include a subject.", logicalName));
            }

        } else {
            throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s does not include a valid action verb at at the start.", logicalName));
        }

        return new ParsedName(logicalName, action, subject, collectionType, preposition, object);
    }
}
