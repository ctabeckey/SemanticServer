package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.utility.URLFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class represents one portion of the application vocabulary,
 * either the  actions (verbs) or the prepositions.
 * Subjects and Objects (nouns) are represented by the model classes.
 */
public class VocabularyImpl implements Vocabulary {
    private final Set<VocabularyWord> words;

    public static VocabularyImpl createDefaultActionsVocabulary() throws CoreRouterSemanticsException {
        try {
            return create(URLFactory.create("rsc:default_actions.csv") );
        } catch (MalformedURLException e) {
            throw new CoreRouterSemanticsException(e);
        }
    }

    public static VocabularyImpl createDefaultPrepositionVocabulary() throws CoreRouterSemanticsException {
        try {
            return create(URLFactory.create("rsc:default_prepositions.csv") );
        } catch (MalformedURLException e) {
            throw new CoreRouterSemanticsException(e);
        }
    }

    /**
     *
     * @param actionVocabularyResource
     * @return
     * @throws CoreRouterSemanticsException
     */
    public static VocabularyImpl create(final URL actionVocabularyResource) throws CoreRouterSemanticsException {
        Set<VocabularyWord> words = new HashSet<>();
        try (LineNumberReader inReader = new LineNumberReader(new InputStreamReader(actionVocabularyResource.openStream()))) {
            for(String line = inReader.readLine(); line != null; line = inReader.readLine()) {
                words.add(VocabularyWordImpl.create(line));
            }

        } catch (IOException e) {
            throw new CoreRouterSemanticsException(e);
        }

        return new VocabularyImpl(words);
    }

    private VocabularyImpl(Set<VocabularyWord> words) {
        this.words = words;
    }

    /**
     * for purposes of lookup the synonyms are case-insensitive
     * @param synonym
     * @return
     */
    @Override
    public VocabularyWord find(CharSequence synonym)
    {
        if (synonym == null)
            return null;

        for (VocabularyWord word : this.words) {
            if (word.matches(synonym)) {
                return word;
            }
        }
        return null;
    }

    /**
     *
     * @param synonym
     * @return
     */
    @Override
    public boolean matchesAny(CharSequence synonym) {
        return find(synonym) != null;
    }


    @Override
    public Pattern getAnyPattern() {
        StringBuilder sb = new StringBuilder();

        for (VocabularyWord word : this.words) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(word.getPattern().pattern());
        }
        return Pattern.compile(sb.toString());
    }
}
