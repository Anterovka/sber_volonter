package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.interfaces.PropertyFromXml;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeChangeException extends CheckXmlModelException {
    public TypeChangeException(PropertyFromXml prevProperty, XmlModelClassProperty curProperty) {
        super(join("Incompatible change that breaks backward compatibility. Changing the type of a property is forbidden.",
                "Error in", propertyInCLass("e", curProperty),
                ". There was a property with the type", prevProperty.getType(), " now ", curProperty.getType()),
            "Return the property type back");
    }
}
