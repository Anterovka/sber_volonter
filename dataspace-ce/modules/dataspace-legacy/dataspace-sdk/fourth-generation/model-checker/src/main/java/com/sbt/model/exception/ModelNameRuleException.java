package com.sbt.model.exception;

import com.sbt.mg.ModelHelper;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: The class name is already set in the base module model
 */
public class ModelNameRuleException extends CheckXmlModelException {
    /**
     * @param modelName Name of the model
     */
    public ModelNameRuleException(String modelName) {
        super(join("Model name", modelName, "did not pass the check. The name must",
            "consist of Latin characters, digits, and the symbol \"_\", start with a Latin character,",
            "not to exceed", ModelHelper.MAX_MODEL_NAME_LENGTH, "characters and not be empty"));
    }
}
