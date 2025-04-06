package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class DefaultEnumValueNotFoundException extends CheckXmlModelException {
    public DefaultEnumValueNotFoundException(XmlModelClassProperty property) {
        super(join("The default value for the enum is incorrectly defined.", propertyInCLass("a", property), "Значение по умолчанию для перечисления определено неверно.",
                "Only values defined in the enum class can be specified", property.getType()),
            join("Fix the value", property.getDefaultValue(), "with a value from the enum list"));
    }
}
