package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.exception.parent.CheckModelException;

public class DefaultPrimitiveValueException extends CheckModelException {
    public DefaultPrimitiveValueException(XmlModelClassProperty property, String causeMessage) {
        super(join("The default value for type {", property.getType(), "} of class {", property.getModelClass().getName(), "} attribute {", property.getName(), "} is incorrectly defined."),
            join("Fix the value of \"", property.getDefaultValue(), "\" to the value according to the documentation.", causeMessage));
    }
}
