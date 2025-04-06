package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ChangeSetAlreadyExistsInCurrentCustomChangelogException extends GeneralSdkException {
    public ChangeSetAlreadyExistsInCurrentCustomChangelogException(String errorText) {
        super(errorText);
    }

}
