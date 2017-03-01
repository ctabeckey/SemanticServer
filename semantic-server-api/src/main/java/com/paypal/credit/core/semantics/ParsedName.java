package com.paypal.credit.core.semantics;

/**
 * Created by cbeckey on 2/13/17.
 */
public class ParsedName {
    private final String logicalName;
    private final VocabularyWord action;
    private final String subject;
    private final CollectionType collectionType;
    private final VocabularyWord preposition;
    private final String object;

    public ParsedName(String logicalName, VocabularyWord action, String subject, CollectionType collectionType, VocabularyWord preposition, String object) {
        this.logicalName = logicalName;
        this.action = action;
        this.subject = subject;
        this.collectionType = collectionType;
        this.preposition = preposition;
        this.object = object;
    }

    public String getLogicalName() {
        return logicalName;
    }

    public VocabularyWord getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }

    public VocabularyWord getPreposition() {
        return preposition;
    }

    public String getObject() {
        return object;
    }
}
