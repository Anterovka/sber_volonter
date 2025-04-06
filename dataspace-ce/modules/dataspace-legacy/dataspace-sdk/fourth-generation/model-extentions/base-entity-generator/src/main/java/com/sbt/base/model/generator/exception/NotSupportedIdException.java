package com.sbt.base.model.generator.exception;

public class NotSupportedIdException extends BaseEntityGeneratorException {
    public NotSupportedIdException(String className) {
        super(join("For identifier in the class reference", className, "only the category MANUAL is allowed."),
            "Fix the category to MANUAL, or even better delete it. It will be set by default.");
    }
}
