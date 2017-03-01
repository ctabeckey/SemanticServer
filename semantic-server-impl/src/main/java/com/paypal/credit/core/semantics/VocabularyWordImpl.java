package com.paypal.credit.core.semantics;

import com.paypal.credit.context.exceptions.ContextInitializationException;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A VocabularyWord represents a single action(verb) or preposition in the application vocabulary.
 */
public class VocabularyWordImpl implements VocabularyWord {
    private final String nominalValue;
    private final String expression;
    private final Pattern pattern;

    /**
     *
     * @param line - formatted as the word in nominal form, a comma, and the word matching regular expression
     */
    public static VocabularyWord create(@NotNull @Min(3) String line) throws CoreRouterSemanticsException {
        String[] wordLine = line.split(",");
        if (wordLine.length == 2) {
            return new VocabularyWordImpl(wordLine[0].trim(), wordLine[1].trim());
        } else {
            throw new CoreRouterSemanticsException(String.format("[%s] does not include both a nominal and pattern form of the vocabulary word.", line));
        }
    }

    private VocabularyWordImpl(String nominalValue, String expression)
    {
        this.nominalValue = nominalValue;
        this.expression = expression;
        this.pattern = Pattern.compile(expression);
    }

    @Override
    public String getExpression()
    {
        return expression;
    }

    @Override
    public String getNominalValue()
    {
        return this.nominalValue;
    }

    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matches(CharSequence charSequence) {
        if (charSequence == null) {
            return false;
        }

        return getPattern().matcher(charSequence).matches();
    }

    @Override
    public String toString() {
        return "VocabularyWordImpl{" +
                "nominalValue='" + nominalValue + '\'' +
                ", expression='" + expression + '\'' +
                ", pattern=" + pattern +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VocabularyWordImpl that = (VocabularyWordImpl) o;
        return Objects.equals(getNominalValue(), that.getNominalValue()) &&
                Objects.equals(getExpression(), that.getExpression()) &&
                Objects.equals(getPattern(), that.getPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNominalValue(), getExpression(), getPattern());
    }
}
