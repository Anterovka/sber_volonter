package com.sbt.model.exception;

import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

public class ExternalTypeIntersectionException extends CheckModelException {

    public ExternalTypeIntersectionException(List<String> intersectedTypes) {
        super(String.format("External types (externalType) and model class names cannot be the same. Matching type names: %s",
                intersectedTypes),
            "Provide unique names for external types and class names.");
    }
}
