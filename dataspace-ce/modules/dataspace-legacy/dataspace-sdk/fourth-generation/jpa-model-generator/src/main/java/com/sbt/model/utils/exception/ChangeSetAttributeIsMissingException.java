package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ChangeSetAttributeIsMissingException extends GeneralSdkException {
    public ChangeSetAttributeIsMissingException(String errorText) {
        super(errorText);
    }
}
