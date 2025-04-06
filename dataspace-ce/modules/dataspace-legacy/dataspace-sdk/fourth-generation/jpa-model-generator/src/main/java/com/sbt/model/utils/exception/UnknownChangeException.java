package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class UnknownChangeException extends GeneralSdkException {
    public UnknownChangeException(String errorText) {
        super(errorText, "List of allowed changes is described in the documentation on creating a customized changelog (custom-changelog)");
    }
}
