package com.sbt.model.exception;

public class EnumNameAsAliasNameException extends EnumNameAsEntityNameException {
    public EnumNameAsAliasNameException(String enumName) {
        super(enumName, "alias type");
    }
}
