package com.sbt.model.dictionary.exceptions;

import com.sbt.mg.exception.GeneralSdkException;

public class DictionaryCheckParentException extends GeneralSdkException {

    public DictionaryCheckParentException() {
        super();
    }

    public DictionaryCheckParentException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "Checking reference data";
    }
}
