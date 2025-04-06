package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class MappedByToUndefinedClassException extends CheckXmlModelException {
    public MappedByToUndefinedClassException(String className, Collection<XmlModelClassProperty> wrongTypeProperties) {
        super(join("In the class", className, "properties with the attribute", XmlModelClassProperty.MAPPED_BY_TAG,
                ", the types of which are not described in the model. Errors in properties", collectClassProperties(wrongTypeProperties)),
            "Check the types in the properties");
    }
}
