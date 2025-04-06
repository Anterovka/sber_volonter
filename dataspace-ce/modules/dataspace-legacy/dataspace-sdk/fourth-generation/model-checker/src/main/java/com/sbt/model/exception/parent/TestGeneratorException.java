package com.sbt.model.exception.parent;

import com.sbt.mg.exception.GeneralSdkException;

public class TestGeneratorException extends GeneralSdkException {

    public TestGeneratorException() {
    }

    public TestGeneratorException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "creation of test model";
    }

}
