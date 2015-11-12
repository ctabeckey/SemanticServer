package com.paypal.credit.core.semantics;

import com.paypal.credit.core.utility.ParameterCheckUtility;

import java.util.regex.Pattern;

/**
 * ENUM of all permissible prepositions with synonyms.
 */
public enum Preposition
{
	BY("BY", new String[] { "[bB]y" }),
	LIKE("LIKE", new String[] { "[lL]ike" }),
	WITH("WITH", new String[] { "[wW]ith" });

    //
    private static final String anyPatternMatch;
    private static final Pattern anyPattern;

    static {
        anyPatternMatch = createAnyPatternMatch();
        anyPattern = Pattern.compile(anyPatternMatch);
    }

    public static String getAnyPatternMatch() {
        return anyPatternMatch;
    }

    public static Pattern getAnyPattern() {
        return anyPattern;
    }

    private String nominalValue;
    private String[] expression;

	Preposition(final String nominalValue, final String[] expression)
	{
        this.nominalValue = nominalValue;
		this.expression = expression;
	}

	public String[] getExpression()
	{
		return expression;
	}

	public String getNominalValue()
	{
		return this.nominalValue;
	}

	// for purposes of lookup the synonyms are case-insensitive
	public static Preposition find(String synonym)
	{
        ParameterCheckUtility.checkParameterNotNull(synonym, "synonym");

		synonym = synonym.toLowerCase();
		for (Preposition preposition : Preposition.values())
			for (String prepositionSynonym : preposition.getExpression())
				if(Pattern.matches(prepositionSynonym, synonym))
					return preposition;
		return null;
	}

	/**
	 * Create a regular expression pattern from the expression of the
	 * elements of this enumeration.
	 */
	private static String createAnyPatternMatch() {
		StringBuilder sbPattern = new StringBuilder();

		for (Preposition value : Preposition.values()) {
			for (String xpression : value.getExpression()) {
				if (sbPattern.length() > 0)
					sbPattern.append('|');
				sbPattern.append(xpression);
			}
		}

		return sbPattern.toString();
	}

}
