package com.sbt.base.model.generator.exception;

public class ExtendedClassChangeIdInInheritedClassException extends BaseEntityGeneratorException {
    public ExtendedClassChangeIdInInheritedClassException(String className) {
        super(join("It is forbidden to define the type of id formation in descendant classes that differs from the base ancestor class",
                className),
            join("Remove the definition of id and set it on the base class, or do not use the setting at all"));
    }
}
