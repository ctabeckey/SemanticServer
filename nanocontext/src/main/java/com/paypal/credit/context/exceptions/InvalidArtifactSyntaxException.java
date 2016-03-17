package com.paypal.credit.context.exceptions;

import com.paypal.credit.context.xml.ArtifactType;

/**
 * Created by cbeckey on 2/11/16.
 */
public class InvalidArtifactSyntaxException extends ContextInitializationException {
    private static String createMessage(final ArtifactType artifactType) {
        return String.format("Exception when creating artifact (%s) %s", artifactType.getId(), artifactType.getClasspath().toString());
    }

    public InvalidArtifactSyntaxException(final ArtifactType artifactType, Throwable cause) {
        super(createMessage(artifactType), cause);
    }
}
