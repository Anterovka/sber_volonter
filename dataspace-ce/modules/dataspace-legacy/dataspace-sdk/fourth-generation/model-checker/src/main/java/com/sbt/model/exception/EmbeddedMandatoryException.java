package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class EmbeddedMandatoryException extends CheckXmlModelException {
    public EmbeddedMandatoryException(XmlModelClass modelClass, Collection<XmlModelClassProperty> properties) {
        super(join("Embedded properties with the tag", XmlModelClassProperty.MANDATORY_TAG,
                "on properties", collectClassProperties(properties), "of class", modelClass.getName()),
            join("Setting the flag", XmlModelClassProperty.MANDATORY_TAG,
                "on embedded properties is meaningless. Set the flag on the fields of the embedded class."));
    }
}
