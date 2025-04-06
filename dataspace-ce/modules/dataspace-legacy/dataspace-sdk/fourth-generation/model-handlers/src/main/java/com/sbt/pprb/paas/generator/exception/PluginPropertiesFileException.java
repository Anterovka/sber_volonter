package com.sbt.pprb.paas.generator.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class PluginPropertiesFileException extends GeneralSdkException {
    public PluginPropertiesFileException(String setting) {
        super(String.format("Error processing file pluginVersion.properties: %s", setting),
            "Check the correctness of the property assignment.");
    }
}
