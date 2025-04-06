package com.sbt.model.exception;

public class EnumNameAsInterfaceNameException extends EnumNameAsEntityNameException {
    public EnumNameAsInterfaceNameException(String enumName) {
        super(enumName, "interface");
    }
}
