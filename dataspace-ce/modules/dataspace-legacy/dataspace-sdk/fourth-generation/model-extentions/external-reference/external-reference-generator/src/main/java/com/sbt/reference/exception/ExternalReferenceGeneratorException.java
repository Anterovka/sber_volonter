package com.sbt.reference.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ExternalReferenceGeneratorException extends GeneralSdkException {
    public ExternalReferenceGeneratorException() {
        super();
    }

    public ExternalReferenceGeneratorException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "checking external links";
    }
}
