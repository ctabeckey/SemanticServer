package com.paypal.credit.core.semantics;

import com.paypal.credit.utility.ParameterCheckUtility;
import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

import java.lang.reflect.Method;

/**
 * ApplicationSemantics defines and acts as a factory for all of the semantic objects
 * in the application.
 */
public class ApplicationSemantics {
    public static final String[] DEFAULT_OBJECT_PACKAGES = new String[] {
            "java.lang",
            "java.util"
    };

    private final ModelVocabulary subjectVocabulary;
    private final ModelVocabulary objectVocabulary;

    public ApplicationSemantics(String... packageNames)
            throws CoreRouterSemanticsException {
        this(null, packageNames);
    }

    public ApplicationSemantics(ClassLoader classLoader, String... packageNames)
            throws CoreRouterSemanticsException {
        ParameterCheckUtility.checkParameterNotNull(packageNames, "packageNames");
        this.subjectVocabulary = new ModelVocabulary(classLoader, packageNames);

        String[] objectPackageNames = new String[packageNames.length + DEFAULT_OBJECT_PACKAGES.length];
        System.arraycopy(packageNames, 0, objectPackageNames, 0, packageNames.length);
        System.arraycopy(DEFAULT_OBJECT_PACKAGES, 0, objectPackageNames, packageNames.length, DEFAULT_OBJECT_PACKAGES.length);
        this.objectVocabulary = new ModelVocabulary(classLoader, packageNames);
    }

    /**
     * The vocabulary of the Subject in a commandprovider name.
     * The Subject Vocabulary is comprised of the simple names of the entities in the
     * application business model (e.g. "Account" in a financial system).
     * @return the Subject Vocabulary
     */
    public ModelVocabulary getSubjectVocabulary() {
        return subjectVocabulary;
    }

    /**
     * The vocabulary of the Object in a commandprovider name,
     * by definition this is the Subject Vocabulary and the Default object packages, which are
     * currently "java.lang" and "java.util"
     * @return the Object Vocabulary
     */
    public ModelVocabulary getObjectVocabulary() {
        return objectVocabulary;
    }

    // ===================================================================================================
    // Create new instances of semantic types from String values
    // ===================================================================================================
    public CommandClassSemantics createCommandClassSemantic(final String name)
            throws CoreRouterSemanticsException {
        return new CommandClassSemantics(this, name);
    }

    public CommandFactoryMethodSemantics createFactoryMethodSemantic(final String name)
            throws CoreRouterSemanticsException {
        return new CommandFactoryMethodSemantics(this, name);
    }

    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(final String name)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemantics(this, name);
    }

    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(final Method method)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemantics(this, method);
    }

    // ===================================================================================================
    // Create new instances of semantic types from constituent values
    // ===================================================================================================
    public CommandClassSemantics createCommandClassSemantic(
            final Action action,
            final String subject,
            final CollectionType collectionType,
            final Preposition preposition,
            final String object)
            throws CoreRouterSemanticsException {
        return new CommandClassSemantics(this, action, subject, collectionType, preposition, object);
    }

    public CommandFactoryMethodSemantics createFactoryMethodSemantic(
            final Action action,
            final String subject,
            final CollectionType collectionType,
            final Preposition preposition,
            final String object)
            throws CoreRouterSemanticsException {
        return new CommandFactoryMethodSemantics(this, action, subject, collectionType, preposition, object);
    }

    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(
            final Action action,
            final String subject,
            final CollectionType collectionType,
            final Preposition preposition,
            final String object)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemantics(this, action, subject, collectionType, preposition, object);
    }

    // ===================================================================================================
    // Convert from one semantic type to another
    // ===================================================================================================

    public CommandClassSemantics createCommandClassSemantic(final AbstractBaseSemantics baseSemantics)
            throws CoreRouterSemanticsException {
        return new CommandClassSemantics(this, baseSemantics);
    }

    public CommandFactoryMethodSemantics createFactoryMethodSemantic(final AbstractBaseSemantics baseSemantics)
            throws CoreRouterSemanticsException {
        return new CommandFactoryMethodSemantics(this, baseSemantics);
    }

    public ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(final AbstractBaseSemantics baseSemantics)
            throws CoreRouterSemanticsException {
        return new ProcessorBridgeMethodSemantics(this, baseSemantics);
    }
}
