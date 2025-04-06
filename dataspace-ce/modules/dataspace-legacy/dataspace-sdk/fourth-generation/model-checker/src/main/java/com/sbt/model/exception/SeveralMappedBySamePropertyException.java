package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class SeveralMappedBySamePropertyException extends CheckXmlModelException {
    public SeveralMappedBySamePropertyException(String className) {
        super(join("It is forbidden to declare collection properties with the same type with the attribute", XmlModelClassProperty.MAPPED_BY_TAG,
                "Error in class", className),
            "Leave one property from the list");
    }
}
