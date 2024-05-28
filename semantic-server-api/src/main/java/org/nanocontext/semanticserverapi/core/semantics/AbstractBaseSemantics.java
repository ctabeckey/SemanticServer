package org.nanocontext.semanticserverapi.core.semantics;

/**
 * Created by cbeckey on 2/23/17.
 */
public interface AbstractBaseSemantics {
    // =======================================================
    // Accessors
    // =======================================================
    ApplicationSemantics getApplicationSemanticsImpl();

    VocabularyWord getAction();

    VocabularyWord getPreposition();

    String getObject();

    CollectionType getCollectionType();

    String getSubject();

    boolean directMapping(AbstractBaseSemantics other);
}
