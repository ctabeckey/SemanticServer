package com.paypal.credit.core.semantics;

import com.paypal.credit.core.utility.ParameterCheckUtility;

import javax.lang.model.element.Element;
import java.util.regex.Pattern;

/**
 * This class defines the limited set of collection types understood as part of a commandprovider name.
 * 
 */
public enum CollectionType
{
	LIST(java.util.List.class, 1),
	MAP(java.util.Map.class, 2),
	SET(java.util.Set.class, 1);

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

    private final Class<?> collectionClass;
	private final int typeArgumentsCount;

	CollectionType(Class<?> collectionClass, int typeArgumentsCount)
	{
        if (collectionClass == null) {
            throw new IllegalArgumentException("'collectionClass' is null and must not be");
        }
        if (typeArgumentsCount <= 0) {
            throw new IllegalArgumentException("'typeArgumentsCount' is less than or equal to zero and must not be");
        }
		this.collectionClass = collectionClass;
		this.typeArgumentsCount = typeArgumentsCount;
	}
	
	/**
	 * @return the collectionClass
	 */
	public Class<?> getCollectionClass()
	{
		return this.collectionClass;
	}
	
	public String getSimpleName()
	{
		return getCollectionClass().getSimpleName();
	}
	
	public int getTypeArgumentsCount()
	{
		return this.typeArgumentsCount;
	}

	/**
	 * 
	 * @param objectSuffix
	 * @return
	 */
	public static CollectionType findByObjectSuffix(String objectSuffix)
	{
        ParameterCheckUtility.checkParameterNotNull(objectSuffix, "objectSuffix");
		for(CollectionType collectionType: values())
			if(collectionType.getSimpleName().equals(objectSuffix))
				return collectionType;
		
		return null;
	}
	
    /**
     *
     * @param modelElementText
     * @return
     */
    public static CollectionType find(final String modelElementText) {
        ParameterCheckUtility.checkParameterNotNull(modelElementText, "modelElementText");

        for(CollectionType collectionType : values())
        {
            String collectionClassName = collectionType.getCollectionClass().getName();
            if(collectionClassName.equals(modelElementText)
                    || collectionType.getSimpleName().equals(modelElementText))
                return collectionType;
        }

        return null;
    }

    /**
     * Create a regular expression pattern from the expression of the
     * elements of this enumeration.
     */
    private static String createAnyPatternMatch() {
        StringBuilder sbPattern = new StringBuilder();

        for (CollectionType value : CollectionType.values()) {
            if (sbPattern.length() > 0)
                sbPattern.append('|');
            sbPattern.append(value.getSimpleName());
        }

        return sbPattern.toString();
    }

}
