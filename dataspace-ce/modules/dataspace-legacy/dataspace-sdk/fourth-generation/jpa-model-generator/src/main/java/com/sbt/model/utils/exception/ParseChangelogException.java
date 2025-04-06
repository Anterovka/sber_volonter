package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ParseChangelogException extends GeneralSdkException {

    public ParseChangelogException(String errorText) {
        super(errorText);
    }
}
