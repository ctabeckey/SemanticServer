package org.nanocontext.semanticserverapi.core.semantics;

import java.util.regex.Pattern;

/**
 * Created by cbeckey on 2/13/17.
 */
public interface Vocabulary {
    /**
     *
     * @param synonym
     * @return
     */
    VocabularyWord find(CharSequence synonym);

    /**
     *
     * @param synonym
     * @return
     */
    boolean matchesAny(CharSequence synonym);

    /**
     * Return a Pattern that will match any of the included vocabulary words.
     *
     * @return
     */
    Pattern getAnyPattern();
}
