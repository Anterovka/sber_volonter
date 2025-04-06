package com.sbt.model.exception;

public class TypeDefNameEqualToClassNameException extends TypeDefNameEqualToEntityNameException {
    public TypeDefNameEqualToClassNameException(String typeDefName) {
        super(typeDefName, "of class");
    }
}
