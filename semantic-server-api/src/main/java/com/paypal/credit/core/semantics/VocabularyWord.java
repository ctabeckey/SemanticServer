package com.paypal.credit.core.semantics;

import java.util.regex.Pattern;

/**
 * Created by cbeckey on 2/13/17.
 */
public interface VocabularyWord {
    String getExpression();

    String getNominalValue();

    Pattern getPattern();

    boolean matches(CharSequence charSequence);
}
