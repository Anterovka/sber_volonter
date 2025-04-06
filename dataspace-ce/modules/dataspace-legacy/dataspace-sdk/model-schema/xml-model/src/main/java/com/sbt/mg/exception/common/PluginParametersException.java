package com.sbt.mg.exception.common;

import com.sbt.mg.exception.GeneralSdkException;

public class PluginParametersException extends GeneralSdkException {
    public PluginParametersException(String message, String solution) {
        super(message, solution);
    }
}
