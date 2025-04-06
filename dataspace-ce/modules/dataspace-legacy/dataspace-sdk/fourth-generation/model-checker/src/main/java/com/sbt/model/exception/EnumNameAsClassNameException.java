package com.sbt.model.exception;

public class EnumNameAsClassNameException extends EnumNameAsEntityNameException {
    public EnumNameAsClassNameException(String enumName) {
        super(enumName, "class");
    }
}
