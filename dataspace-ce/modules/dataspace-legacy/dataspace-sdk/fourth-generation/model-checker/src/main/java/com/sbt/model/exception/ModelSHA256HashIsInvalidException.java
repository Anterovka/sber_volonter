package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ModelSHA256HashIsInvalidException extends CheckXmlModelException {
    public ModelSHA256HashIsInvalidException(String modelName) {
        super(String.format("In the file pdm.xml, the original model saved during generation in the <source-models> element (model-name = %s) is broken - the SHA256 hash does not match the contents", modelName),
            "Restore the file pdm.xml from the saved copy");
    }
}
