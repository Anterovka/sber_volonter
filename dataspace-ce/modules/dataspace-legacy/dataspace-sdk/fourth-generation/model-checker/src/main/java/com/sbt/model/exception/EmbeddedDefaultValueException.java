package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class EmbeddedDefaultValueException extends CheckXmlModelException {
    public EmbeddedDefaultValueException(XmlModelClass modelClass, Collection<XmlModelClassProperty> properties) {
        super(join("Embedded properties with default values on properties have been detected",
                collectClassProperties(properties), "of class", modelClass.getName()),
            join("Set the default value in the fields of the embedded class."));
    }
}
