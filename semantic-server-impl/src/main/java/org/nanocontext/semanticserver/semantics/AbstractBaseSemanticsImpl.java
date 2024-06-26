package org.nanocontext.semanticserver.semanticserver.semantics;

import org.nanocontext.semanticserverapi.core.semantics.*;
import org.nanocontext.semanticserverapi.core.semantics.exceptions.CoreRouterSemanticsException;

import javax.validation.constraints.NotNull;

/**
 * Most Important:
 * Semantics are defined in language (as in English) terms, that is there are "subject", "object",
 * and "prepositions".
 * A commandprovider always has an Action and a Subject.
 * A commandprovider may have a CollectionType, which is a modifier of the Subject.
 * A commandprovider may have a Preposition and an Object, but never just one of them.
 *
 * "GetAuthorization" is a valid commandprovider
 *  "Get" is the Action,
 *  "Authorization" is the Subject
 * "GetAuthorizationList" is a valid commandprovider,
 *  "Get is the Action,
 *  "Authorization" is the subject,
 *  "List" is the Subject Collection Type
 * "GetAuthorizationByAuthorizationId" is a valid commandprovider,
 *  "Get" is the Action,
 *  "Authorization" is the Subject
 *  "By" is the Preposition
 *  "AuthorizationId" is the Object of the Preposition
 *
 * The semantics of the commandprovider, commandprovider factory methods and the router interface methods
 * are strictly specified in this package.  This strictness
 * is required to allow the reliable generation of the applicationbridge router implementations.
 * This class is the base class for the Command Class semantics, the Command Factory method
 * semantics and the Facade to Router interface method semantics.
 *
 * The Command Class Semantics controls the naming of commandprovider implementations.
 * The Command Factory semantics controls the semantics of the methods on factory classes that
 * create Command Class instances.
 * The Facade to Router interface methods are the methods defined in the interface presented
 * to facades implementations.
 * 
 * Router methods MUST follow the naming convention prescribed below.
 * Methods are named as follows:
 * action + object + optional(preposition + related_object)
 * - action describes what is to be done with the object
 * - object describes the object type being acted on
 * - the optional preposition and object describe the relationship of additional objects to the actio
 *   being taken
 * - the optional preposition and related_object are only required when it is necessary to differentiate
 *   between commandprovider with similar action/object pairs
 * - method names use camel-casing, where the first char of each word is capitalized, except that the
 *   first character of the first word is always lower-case
 * Neither the action name, the object name, nor the related_object name may include any of the words
 * reserved for the action or the preposition.
 * examples: getStudyList(), getStudyListByStudyFilter()
 * The action MUST be one of POST, GET, PUT, DELETE, READ, UPDATE (see Action enumeration)
 * The object MUST be the simple name of a business object (a member of the business model
 *   with an optional collection type (Set, List, Map) concatenated.
 * The preposition MUST be one of BY, LIKE (see Preposition enum)
 * The related_object MUST be either the simple name of a business object or the simple name of a
 * core Java class (java.lang.*)
 * 
 * Command names follow the same semantics as the router names with the String "Command" suffixed.
 * - commandprovider names are camel-cased with the first letter always capitalized (as in a class name).
 * - e.g. CreateAccountImpl, CreateAuthorizationImpl, GetStatementByDateImpl
 *
 * Command factory methods follow the same semantics as the Command names with the String "create" prefixed.
 * - commandprovider factory method names are camel-cased with the first character lower-case (as in a method name).
 * - e.g. createCreateAccount, createCreateAuthorization, createGetStatementByDate
 *
 */
public abstract class AbstractBaseSemanticsImpl implements AbstractBaseSemantics {
    private final ApplicationSemantics applicationSemantics;
	private final VocabularyWord action;
	private final String subject;
    private final Class<?> subjectClass;
	private final CollectionType collectionType;
	private final VocabularyWord preposition;
	private final String object;
    private final Class<?> objectClass;

    /**
     *
     * @param applicationSemantics
     * @param source
     */
    protected AbstractBaseSemanticsImpl(
            final ApplicationSemantics applicationSemantics,
            final AbstractBaseSemantics source) {
        this(applicationSemantics,
                source == null ? null : source.getAction(),
                source == null ? null : source.getSubject(),
                source == null ? null : source.getCollectionType(),
                source == null ? null : source.getPreposition(),
                source == null ? null : source.getObject());
    }

    /**
     * Parse the logical name into constituent parts.
     * Derived classes should trim their type-specific portion.
     *
     * @param applicationSemantics
     * @param logicalCommandName
     * @throws CoreRouterSemanticsException
     */
    protected AbstractBaseSemanticsImpl(
			@NotNull final ApplicationSemantics applicationSemantics,
			@NotNull final String logicalCommandName)
    throws CoreRouterSemanticsException
    {
        this.applicationSemantics = applicationSemantics;
		ParsedName name = this.applicationSemantics.getLogicalNameParserImpl().parse(logicalCommandName);

        this.action = name.getAction();
        this.subject = name.getSubject();
        this.collectionType = name.getCollectionType();
        this.preposition = name.getPreposition();
        this.object = name.getObject();

        this.subjectClass = findSubjectClass(this.subject);
        this.objectClass = findObjectClass(this.object);
    }

    /**
     * Create an instance of a CoreRouterSemantics with all elements
     *
	 * @param action
	 * @param subject
     * @param collectionType
	 * @param preposition
	 * @param object
	 */
	protected AbstractBaseSemanticsImpl(
			@NotNull final ApplicationSemantics applicationSemantics,
			@NotNull final VocabularyWord action,
			@NotNull final String subject,
            final CollectionType collectionType,
            final VocabularyWord preposition,
            final String object)
	{
        this.applicationSemantics = applicationSemantics;
        this.action = action;
        this.subject = subject;
        this.collectionType = collectionType;
        this.preposition = preposition;
        this.object = object;

        this.subjectClass = findSubjectClass(this.subject);
        this.objectClass = findObjectClass(this.object);
	}

    private Class<?> findSubjectClass(final String subject) {
        ModelVocabulary subjectVocabulary = this.applicationSemantics.getSubjectVocabulary();
        return subjectVocabulary.getClass(subject);
    }

    private Class<?> findObjectClass(final String object) {
        ModelVocabulary objectVocabulary = this.applicationSemantics.getObjectVocabulary();
        return objectVocabulary.getClass(object);
    }

    // =======================================================
    // Accessors
    // =======================================================
    public ApplicationSemantics getApplicationSemanticsImpl() {
        return this.applicationSemantics;
    }

	public VocabularyWord getAction()
	{
		return this.action;
	}
	
	public String getSubject()
	{
		return this.subject;
	}
	
	public CollectionType getCollectionType()
	{
		return this.collectionType;
	}
	
	public VocabularyWord getPreposition()
	{
		return preposition;
	}
	
	public String getObject()
	{
		return object;
	}

    // =======================================================
    // toString()
    // =======================================================
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String actionName = getAction().getNominalValue().toLowerCase();
        actionName = SemanticsUtility.setFirstCharCase(actionName, true);
        sb.append(actionName);

        sb.append(getSubject());

        if (getCollectionType() != null) {
            sb.append(getCollectionType().getSimpleName());
        }
        if (getPreposition() != null) {
            String preposition = getPreposition().getNominalValue().toLowerCase();
            preposition = SemanticsUtility.setFirstCharCase(preposition, true);
            sb.append(preposition);

            sb.append(getObject());
        }

        return sb.toString();
    }


    // =======================================================
    // equals and hashCode
    // =======================================================
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.action == null) ? 0 : this.action.hashCode());
		result = prime
				* result
				+ ((this.collectionType == null) ? 0
						: this.collectionType.hashCode());
		result = prime
				* result
				+ ((this.subject == null) ? 0 : this.subject
						.hashCode());
		result = prime
				* result
				+ ((this.preposition == null) ? 0 : this.preposition.hashCode());
		result = prime
				* result
				+ ((this.object == null) ? 0 : this.object
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractBaseSemanticsImpl other = (AbstractBaseSemanticsImpl) obj;

        return directMapping(other);
	}

	/**
	 * This method compares to instances of CoreRouterSemantics (or derivations)
	 * for equivalency in mapping.  
	 * Essentially this means that a router commandprovider can be mapped to a commandprovider type,
	 * which can be mapped to a commandprovider factory method.
	 * 
	 * examples:
	 * "getStudy" directly maps to "GetStudyCommand" and "createGetStudyCommand"
	 * "getStudyListByStudyFilter" directly maps to "GetStudyListByStudyFilterCommand" and "createGetStudyListByStudyFilterCommand"
	 * 
	 * @param other
	 * @return
	 */
	public boolean directMapping(final AbstractBaseSemantics other)
	{
		if (this.action == null)
		{
			if (other.getAction() != null)
				return false;
		} else if (!this.action.equals(other.getAction()))
			return false;
		if (this.collectionType == null)
		{
			if (other.getCollectionType() != null)
				return false;
		} else if (!this.collectionType
				.equals(other.getCollectionType()))
			return false;
		if (this.subject == null)
		{
			if (other.getSubject() != null)
				return false;
		} else if (!this.subject.equals(other.getSubject()))
			return false;
		if (this.preposition == null)
		{
			if (other.getPreposition() != null)
				return false;
		} else if (!this.preposition.equals(other.getPreposition()))
			return false;
		if (this.object == null)
		{
			if (other.getObject() != null)
				return false;
		} else if (!this.object.equals(other.getObject()))
			return false;

		return true;
	}
}
