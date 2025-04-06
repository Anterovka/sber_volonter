package com.sbt.model.exception.diff;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class TypeReduceException extends CheckXmlModelException {
    public TypeReduceException(XmlModelClassProperty property, String prevType, String curType) {
        super(join("Noticed narrowing of type", propertyInCLass("a", property),
                "The property was of type", prevType, "and became", curType),
            "Return the old value of the type or extend the value");
    }
}
