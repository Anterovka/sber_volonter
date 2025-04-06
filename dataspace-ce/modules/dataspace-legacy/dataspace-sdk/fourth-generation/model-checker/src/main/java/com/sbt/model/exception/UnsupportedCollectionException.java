package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class UnsupportedCollectionException extends CheckXmlModelException {
    public UnsupportedCollectionException(String className, Collection<XmlModelClassProperty> property) {
        super(join("Collection type: list is not supported. Error found in properties",
                collectClassProperties(property), "of class", className),
            "Supported is a collection of type set");
    }
}
