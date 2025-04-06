package com.sbt.model.exception;

public class TypeDefNameEqualToInterfaceNameException extends TypeDefNameEqualToEntityNameException {
    public TypeDefNameEqualToInterfaceNameException(String typeDefName) {
        super(typeDefName, "interface");
    }
}
