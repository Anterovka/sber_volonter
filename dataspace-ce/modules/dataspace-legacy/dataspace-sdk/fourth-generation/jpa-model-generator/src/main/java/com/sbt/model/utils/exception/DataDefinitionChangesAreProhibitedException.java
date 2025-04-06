package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class DataDefinitionChangesAreProhibitedException extends GeneralSdkException {
    public DataDefinitionChangesAreProhibitedException(String errorText) {
        super(errorText);
    }
}
