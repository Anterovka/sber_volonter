package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ExternalReferenceDefaultValueException extends GeneralSdkException {

    public ExternalReferenceDefaultValueException(String className, String propertyName) {
        super(String.format("The functionality of updating links to an external aggregate with a default value" +
                "is not supported. The %s property of the %s class.", propertyName, className),
            "Remove the default value (default-value).");
    }
}
