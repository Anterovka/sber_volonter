package com.sbt.status.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class XmlStatusException extends GeneralSdkException {
    public XmlStatusException() {
        super();
    }
    public XmlStatusException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "primary processing of status integrity during XML reading";
    }
}
