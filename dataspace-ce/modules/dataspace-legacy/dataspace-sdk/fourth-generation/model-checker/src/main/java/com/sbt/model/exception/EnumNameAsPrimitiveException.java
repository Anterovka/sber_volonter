package com.sbt.model.exception;

public class EnumNameAsPrimitiveException extends EnumNameAsEntityNameException {
    public EnumNameAsPrimitiveException(String enumName) {
        super(enumName, "примитива");
    }
}
