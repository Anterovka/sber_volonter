package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ChangeSetAlreadyExistsInPreviousChangelogException extends GeneralSdkException {
    public ChangeSetAlreadyExistsInPreviousChangelogException(String errorText) {
        super(errorText);
    }

}
