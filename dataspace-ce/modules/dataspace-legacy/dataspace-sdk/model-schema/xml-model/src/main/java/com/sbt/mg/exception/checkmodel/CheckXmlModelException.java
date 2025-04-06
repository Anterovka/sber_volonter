package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.GeneralSdkException;

public class CheckXmlModelException extends GeneralSdkException {

    public CheckXmlModelException() {
        super();
    }

    public CheckXmlModelException(String message) {
        super(message);
    }

    public CheckXmlModelException(String message, String solution) {
        super(message, solution);
    }

    @Override
    public String getPosition() {
        return "checking XML description";
    }
}
