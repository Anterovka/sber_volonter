package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ComputedExpressionChangeException extends CheckXmlModelException {
    public ComputedExpressionChangeException(XmlModelClassProperty prevProperty, XmlModelClassProperty newProperty) {
        super(join("Incompatible change that breaks backward compatibility. Changing computed expression of property is not allowed.",
                "Error in", propertyInCLass("e", newProperty),
                ". Old expression ", prevProperty.getComputedExpression(), "new expression", newProperty.getComputedExpression()),
            "Return the computed expression back.");
    }
}
