package com.sbt.status.exception;

public class DuplicateStatusClassesException extends XmlStatusException {
    public DuplicateStatusClassesException(String stakeholderCode, String statusCode) {
        super(join("Duplication of status codes is detected: \"", statusCode, "\" in the observer with code", stakeholderCode),
            "The ambiguity must be corrected");
    }
}
