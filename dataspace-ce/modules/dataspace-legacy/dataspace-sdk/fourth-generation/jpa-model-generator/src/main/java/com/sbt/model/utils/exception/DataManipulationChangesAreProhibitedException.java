package com.sbt.model.utils.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class DataManipulationChangesAreProhibitedException extends GeneralSdkException {
    public DataManipulationChangesAreProhibitedException(String errorText) {
        super(errorText);
    }
}
