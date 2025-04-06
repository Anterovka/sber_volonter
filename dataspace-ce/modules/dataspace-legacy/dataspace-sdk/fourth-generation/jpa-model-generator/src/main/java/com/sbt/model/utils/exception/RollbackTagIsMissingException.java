package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class RollbackTagIsMissingException extends GeneralSdkException {
    public RollbackTagIsMissingException(String errorText) {
        super(errorText);
    }
}
