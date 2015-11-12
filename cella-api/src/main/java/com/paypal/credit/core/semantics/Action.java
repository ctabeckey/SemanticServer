package com.paypal.credit.core.semantics;

import java.util.regex.Pattern;

/**
 * ENUM of all permissible action verbs, with synonyms.
 */
public enum Action
{
	GET("GET", new String[]{ "[gG]et", "[rR]ead" }),
	POST("POST", new String[]{ "[pP]ost", "[cC]reate"}),
	PUT("PUT", new String[]{ "[pP]ut", "[uU]pdate" }),
	DELETE("DELETE", new String[]{ "[dD]elete" }),
    SUBMIT("SUBMIT", new String[]{ "[sS]ubmit" });

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

	Action(String nominalValue, String[] expression)
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

    /**
     * for purposes of lookup the synonyms are case-insensitive
     * @param synonym
     * @return
     */
	public static Action find(String synonym)
	{
		if (synonym == null)
			return null;

		for (Action action : Action.values())
			for (String actionSynonym : action.getExpression())
				if(Pattern.matches(actionSynonym, synonym))
					return action;
		return null;
	}

	/**
	 * Create a regular expression pattern from the expression of the
     * elements of this enumeration.
	 */
	private static String createAnyPatternMatch() {
		StringBuilder sbPattern = new StringBuilder();

		for (Action actionValue : Action.values()) {
            for (String actionExpression : actionValue.getExpression()) {
                if (sbPattern.length() > 0)
                    sbPattern.append('|');
                sbPattern.append(actionExpression);
            }
		}

		return sbPattern.toString();
	}

}
