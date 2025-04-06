package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class UnsupportedLinkInEmbeddableClassException extends CheckXmlModelException {
    public UnsupportedLinkInEmbeddableClassException(XmlModelClass embeddableClass, Collection<XmlModelClassProperty> properties) {
        super(join("In embedded classes, defining links is prohibited. Property errors occur",
                collectClassProperties(properties), "of class", embeddableClass.getName()),
            "Determine the specified properties in non-embeddable classes");
    }
}
