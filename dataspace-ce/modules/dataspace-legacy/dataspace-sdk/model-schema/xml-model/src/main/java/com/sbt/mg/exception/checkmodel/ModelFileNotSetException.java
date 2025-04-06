package com.sbt.mg.exception.checkmodel;

/**
 * Exception: No model file is set
 */
public class ModelFileNotSetException extends CheckXmlModelException {
    public ModelFileNotSetException(String filePath, String modelName) {
        super(join("The plugin for processing the model did not find a file with the name", modelName,
                "along the way", filePath),
            "Join the model file creation or specify the correct path in the plugin settings");
    }
}
