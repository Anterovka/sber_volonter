package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ReferenceDefaultValueException extends GeneralSdkException {

    public ReferenceDefaultValueException(String className, String propertyName) {
        super(String.format("The functionality of updating links with a default value " +
                "is not supported. The %s property of the %s class.", propertyName, className),
            "Remove the default value (default-value).");
    }
}
