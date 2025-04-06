package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.GeneralSdkException;

public class NoMatchesFoundInLineByRegexException extends GeneralSdkException {
    public NoMatchesFoundInLineByRegexException(String errorText) {
        super(errorText);
    }
}
