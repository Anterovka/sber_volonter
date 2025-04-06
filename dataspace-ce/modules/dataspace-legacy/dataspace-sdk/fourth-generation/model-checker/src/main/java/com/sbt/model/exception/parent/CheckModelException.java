package com.sbt.model.exception.parent;

import com.sbt.mg.exception.GeneralSdkException;

public class CheckModelException extends GeneralSdkException {

    public CheckModelException() {
    }

    public CheckModelException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "checking the integrity of the model";
    }

}
