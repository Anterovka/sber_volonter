package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ParentPropertyNameAsChildException extends CheckXmlModelException {

    public ParentPropertyNameAsChildException(XmlModelClassProperty firstProperty, XmlModelClassProperty secondProperty) {
        super(join("Creating equivalent names for properties in inherited classes is prohibited.",
                "In the class", firstProperty.getModelClass().getName(),
                "a property with the name", firstProperty.getName(),
                ", also defined is an identical property", secondProperty.getName(),
                "in the class", secondProperty.getModelClass().getName()),
            join("Change the property name", firstProperty.getName(),
                "in the class", firstProperty.getModelClass().getName(), "or property",
                secondProperty.getName(), "in class", secondProperty.getModelClass().getName()));
    }
}
