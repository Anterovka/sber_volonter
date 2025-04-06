package com.sbt.base.model.generator.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class BaseEntityGeneratorException extends GeneralSdkException {
    public BaseEntityGeneratorException() {
        super();
    }

    public BaseEntityGeneratorException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "Functionality: basic entity";
    }
}
