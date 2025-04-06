package com.sbt.dataspace.pdm.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class PluginPropertiesException extends GeneralSdkException {
    public PluginPropertiesException(String errorText) {
        super(String.format("Error in plugin parameters: %s", errorText));
    }
}
