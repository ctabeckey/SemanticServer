package com.paypal.credit.core.semantics;

import com.paypal.credit.core.semantics.exceptions.CoreRouterSemanticsException;

import java.lang.reflect.Method;

/**
 * Created by cbeckey on 2/24/17.
 */
public interface ApplicationSemantics {
    Vocabulary getActionVocabulary();

    ModelVocabulary getSubjectVocabulary();

    Vocabulary getPrepositionVocabulary();

    ModelVocabulary getObjectVocabulary();

    LogicalNameParser getLogicalNameParserImpl();

    // ===================================================================================================
    // Create new instances of semantic types from String values
    // ===================================================================================================
    CommandClassSemantics createCommandClassSemantic(String name)
            throws CoreRouterSemanticsException;

    CommandFactoryMethodSemantics createFactoryMethodSemantic(String name)
                    throws CoreRouterSemanticsException;

    ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(String name)
                            throws CoreRouterSemanticsException;

    ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(Method method)
                                    throws CoreRouterSemanticsException;

    // ===================================================================================================
    // Create new instances of semantic types from constituent values
    // ===================================================================================================
    CommandClassSemantics createCommandClassSemantic(
            VocabularyWord action,
            String subject,
            CollectionType collectionType,
            VocabularyWord preposition,
            String object)
            throws CoreRouterSemanticsException;

    CommandFactoryMethodSemantics createFactoryMethodSemantic(
            VocabularyWord action,
            String subject,
            CollectionType collectionType,
            VocabularyWord preposition,
            String object)
                    throws CoreRouterSemanticsException;

    ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(
            VocabularyWord action,
            String subject,
            CollectionType collectionType,
            VocabularyWord preposition,
            String object)
                            throws CoreRouterSemanticsException;

    CommandClassSemantics createCommandClassSemantic(AbstractBaseSemantics baseSemantics)
                                    throws CoreRouterSemanticsException;

    CommandFactoryMethodSemantics createFactoryMethodSemantic(AbstractBaseSemantics baseSemantics)
                                            throws CoreRouterSemanticsException;

    ProcessorBridgeMethodSemantics createProcessorBridgeMethodSemantics(AbstractBaseSemantics baseSemantics)
                                                    throws CoreRouterSemanticsException;
}
