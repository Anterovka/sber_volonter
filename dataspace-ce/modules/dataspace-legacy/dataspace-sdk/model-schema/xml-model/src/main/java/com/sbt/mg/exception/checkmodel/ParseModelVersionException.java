package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.GeneralSdkException;

public class ParseModelVersionException extends GeneralSdkException {

    public ParseModelVersionException(String errorText, String solution) {
        super(errorText, solution);
    }
}
