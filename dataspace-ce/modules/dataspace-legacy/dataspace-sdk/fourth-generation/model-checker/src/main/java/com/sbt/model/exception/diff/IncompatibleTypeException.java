package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IncompatibleTypeException extends CheckXmlModelException {
    public IncompatibleTypeException(XmlModelClassProperty property, String prevType, String curType) {
        super(join("Incompatible type change is noticed in the property", property.getName(),
                "in the class", property.getModelClass().getName(), ". The previous property was of type", prevType, "has become", curType),
            "The type should not change");
    }
}
