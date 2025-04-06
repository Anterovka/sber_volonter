package com.sbt.base.model.generator.exception;

public class NotSupportedIdTypeException extends BaseEntityGeneratorException {
    public NotSupportedIdTypeException(String className, String type) {
        super(join("For identifier type in class", className, "you can only specify String type,",
                "other types are not supported"),
            join("Fix the type", type, "to the String type"));
    }
}
