package com.sbt.model.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class UnsupportedBackwardIncompatibleChangeException extends GeneralSdkException {
    public UnsupportedBackwardIncompatibleChangeException(String errorText) {
        super(String.format("Backward-incompatible change [%s] is not supported yet", errorText));
    }
}
