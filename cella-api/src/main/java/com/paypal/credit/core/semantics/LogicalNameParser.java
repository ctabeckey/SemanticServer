package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

import java.util.regex.Matcher;

/**
 * Parses the logical name into Action, Subject (CollectionType)(, Preposition, and Object)
 * This DOES NOT validate the Subject or Object against the application model, that is the
 * responsibility of the AbstractBaseSemantics class.
 * Note that names like "GetThingCollection" will return a Subject of "ThingCollection",
 * the end of the Subject is either the start of a CollectionType, Preposition or the end of
 * the String.
 */
class LogicalNameParser {
    private final String logicalName;
    private final Action action;
    private final String subject;
    private final CollectionType collectionType;
    private final Preposition preposition;
    private final String object;

    /**
     * Parse a String like this:
     * ActionSubject[Collection][PrepositionObject]
     * into Action, Subject, Collection, Preposition and Object
     *
     * @param logicalName
     * @throws CoreRouterSemanticsException
     */
    LogicalNameParser(final String logicalName)
            throws CoreRouterSemanticsException {
        // save the original value
        this.logicalName = logicalName;

        // extract the Action verb, which must be at the start
        Matcher actionMatcher = Action.getAnyPattern().matcher(logicalName);
        if (actionMatcher.find() && actionMatcher.start() == 0) {
            String actionIdentifier = logicalName.substring(actionMatcher.start(), actionMatcher.end());
            this.action = Action.find(actionIdentifier);

            int endOfSubject = -1;
            Matcher collectionTypeMatcher = CollectionType.getAnyPattern().matcher(logicalName);
            if (collectionTypeMatcher.find(actionMatcher.end())) {
                String collectionIdentifier = logicalName.substring(collectionTypeMatcher.start(), collectionTypeMatcher.end());
                endOfSubject = collectionTypeMatcher.start();
                this.collectionType = CollectionType.find(collectionIdentifier);
                if (this.collectionType == null) {
                    throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s includes an unknown collection type.", logicalName));
                }
            } else {
                this.collectionType = null;
            }

            Matcher prepositionMatcher = Preposition.getAnyPattern().matcher(logicalName);
            if (prepositionMatcher.find(Math.max(actionMatcher.end(), endOfSubject))) {
                String prepositionIdentifier = logicalName.substring(prepositionMatcher.start(), prepositionMatcher.end());
                if (endOfSubject == -1) {
                    endOfSubject = prepositionMatcher.start();
                }
                this.preposition = Preposition.find(prepositionIdentifier);
                if (this.preposition == null) {
                    throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s includes an unknown preposition.", logicalName));
                }
                this.object = logicalName.substring(prepositionMatcher.end());
                if (this.object == null || this.object.isEmpty()) {
                    throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s included a preposition but does not include an object.", logicalName));
                }
            } else {
                this.preposition = null;
                this.object = null;
            }

            if (endOfSubject == -1) {
                endOfSubject = logicalName.length();
            }

            this.subject = logicalName.substring(actionMatcher.end(), endOfSubject);
            if (this.subject == null || this.subject.isEmpty()) {
                throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s does not include a subject.", logicalName));
            }

        } else {
            throw new CoreRouterSemanticsException(String.format("Logical commandprovider name %s does not include a valid action verb at at the start.", logicalName));
        }
    }

    public String getLogicalName() {
        return logicalName;
    }

    public Action getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public Preposition getPreposition() {
        return preposition;
    }

    public String getObject() {
        return object;
    }
}
