package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IncompatibleTypeConversionException extends CheckXmlModelException {
    public IncompatibleTypeConversionException(XmlModelClassProperty prevProperty, XmlModelClassProperty newProperty) {
        super(join("Noticed incompatible type conversion in property", prevProperty.getName(),
                "in the class", prevProperty.getModelClass().getName(),
                "The transformation of types Date -> LocalDateTime is possible only for length=\"3\" (as defined by default) for the LocalDateTime type"),
            String.format("Delete the length attribute = %s, (it will be set by default to 3) in the property [%s] of the class [%s], or explicitly set it to 3",
                newProperty.getLength(), prevProperty.getName(), prevProperty.getModelClass().getName()));
    }
}
