package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;
import com.paypal.utility.ParameterCheckUtility;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

/**
 * ApplicationSemantics defines and acts as a factory for all of the semantic objects
 * in the application.
 */
public class ApplicationSemanticsImpl implements ApplicationSemantics {
    public static final String[] DEFAULT_OBJECT_PACKAGES = new String[] {
            "java.lang",
            "java.util"
    };

    public static ApplicationSemanticsImpl create(String packageName)
            throws CoreRouterSemanticsException {
        return create(null, new String[]{packageName});
    }

    public static ApplicationSemanticsImpl create(String... packageNames)
            throws CoreRouterSemanticsException {
        return create(null, packageNames);
    }

    public static ApplicationSemanticsImpl create(ClassLoader classLoader, String[] packageNames)
            throws CoreRouterSemanticsException {
        ParameterCheckUtility.checkParameterNotNull(packageNames, "packageNames");
        ClassLoader effectiveClassLoader = classLoader == null ? ApplicationSemanticsImpl.class.getClassLoader() : classLoader;

        Vocabulary actionVocabulary = VocabularyImpl.createDefaultActionsVocabulary();

        ModelVocabulary subjectVocabulary = ModelVocabularyImpl.create(classLoader, packageNames);

        Vocabulary prepositionVocabulary = VocabularyImpl.createDefaultPrepositionVocabulary();

        String[] objectPackageNames = new String[packageNames.length + DEFAULT_OBJECT_PACKAGES.length];
        System.arraycopy(packageNames, 0, objectPackageNames, 0, packageNames.length);
        System.arraycopy(DEFAULT_OBJECT_PACKAGES, 0, objectPackageNames, packageNames.length, DEFAULT_OBJECT_PACKAGES.length);
        ModelVocabulary objectVocabulary = ModelVocabularyImpl.create(classLoader, packageNames);

        return new ApplicationSemanticsImpl(effectiveClassLoader, actionVocabulary, subjectVocabulary, prepositionVocabulary, objectVocabulary);
    }

    // ==========================================================================================
    // Instance Members
    // ==========================================================================================

    private final ClassLoader classLoader;
    private final Vocabulary actionVocabulary;
    private final ModelVocabulary subjectVocabulary;
    private final Vocabulary prepositionVocabulary;
    private final ModelVocabulary objectVocabulary;

    public ApplicationSemanticsImpl(
            final ClassLoader classLoader,
            @NotNull final Vocabulary actionVocabulary,
            @NotNull final ModelVocabulary subjectVocabulary,
            @NotNull final Vocabulary prepositionVocabulary,
            @NotNull final ModelVocabulary objectVocabulary) {
        this.classLoader = classLoader;
        this.actionVocabulary = actionVocabulary;
        this.subjectVocabulary = subjectVocabulary;
        this.prepositionVocabulary = prepositionVocabulary;
        this.objectVocabulary = objectVocabulary;
    }

    /**
     *
     * @return
     */
    @Override
    public Vocabulary getActionVocabulary() {
        return actionVocabulary;
    }

    /**
     * The vocabulary of the Subject in a commandprovider name.
     * The Subject Vocabulary is comprised of the simple names of the entities in the
     * application business model (e.g. "Account" in a financial system).
     * @return the Subject Vocabulary
     */
    @Override
    public ModelVocabulary getSubjectVocabulary() {
        return subjectVocabulary;
    }

    /**
     *
     * @return
     */
    @Override
    public Vocabulary getPrepositionVocabulary() {
        return prepositionVocabulary;
    }

    /**
     * The vocabulary of the Object in a commandprovider name,
     * by definition this is the Subject Vocabulary and the Default object packages, which are
     * currently "java.lang" and "java.util"
     * @return the Object Vocabulary
     */
    @Override
    public ModelVocabulary getObjectVocabulary() {
        return objectVocabulary;
    }

    private LogicalNameParser logicalNameParser = null;

    public synchronized LogicalNameParser getLogicalNameParserImpl() {
        if (logicalNameParser == null) {
            this.logicalNameParser = new LogicalNameParserImpl(actionVocabulary, subjectVocabulary, prepositionVocabulary, objectVocabulary);
        }
        return logicalNameParser;
    }

    // ===================================================================================================
    // Create new instances of semantic types from String values
    // ===================================================================================================
    @Override
    public CommandClassSemantics createCommandClassSemantic(final String name)
            throws CoreRouterSemanticsException {
        return new CommandClassSemanticsImpl(this, name);
    }

    @Override
    public CommandFactoryMethodSemantics createFactoryMethodSemantic(final String name)
            throws CoreRouterSemanticsException {
        return new CommandFactoryMethodSemanticsImpl(this, name);
    }

    @Override
    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(final String name)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemanticsImpl(this, name);
    }

    @Override
    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(final Method method)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemanticsImpl(this, method);
    }

    // ===================================================================================================
    // Create new instances of semantic types from constituent values
    // ===================================================================================================
    @Override
    public CommandClassSemantics createCommandClassSemantic(
            final VocabularyWord action,
            final String subject,
            final CollectionType collectionType,
            final VocabularyWord preposition,
            final String object)
            throws CoreRouterSemanticsException {
        return new CommandClassSemanticsImpl(this, action, subject, collectionType, preposition, object);
    }

    @Override
    public CommandFactoryMethodSemantics createFactoryMethodSemantic(
            final VocabularyWord action,
            final String subject,
            final CollectionType collectionType,
            final VocabularyWord preposition,
            final String object)
            throws CoreRouterSemanticsException {
        return new CommandFactoryMethodSemanticsImpl(this, action, subject, collectionType, preposition, object);
    }

    @Override
    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(
            final VocabularyWord action,
            final String subject,
            final CollectionType collectionType,
            final VocabularyWord preposition,
            final String object)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemanticsImpl(this, action, subject, collectionType, preposition, object);
    }

    // ===================================================================================================
    // Convert from one semantic type to another
    // ===================================================================================================

    public CommandClassSemanticsImpl createCommandClassSemantic(final AbstractBaseSemantics baseSemantics)
            throws CoreRouterSemanticsException {
        return new CommandClassSemanticsImpl(this, baseSemantics);
    }

    public CommandFactoryMethodSemanticsImpl createFactoryMethodSemantic(final AbstractBaseSemantics baseSemantics)
            throws CoreRouterSemanticsException {
        return new CommandFactoryMethodSemanticsImpl(this, baseSemantics);
    }

    public ProcessorBridgeMethodSemanticsImpl createProcessorBridgeMethodSemantics(final AbstractBaseSemantics baseSemantics)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemanticsImpl(this, baseSemantics);
    }
}
