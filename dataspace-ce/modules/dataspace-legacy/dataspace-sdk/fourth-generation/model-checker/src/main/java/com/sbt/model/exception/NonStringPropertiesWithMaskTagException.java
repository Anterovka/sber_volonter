package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class NonStringPropertiesWithMaskTagException extends CheckXmlModelException {
    public NonStringPropertiesWithMaskTagException(XmlModelClass modelClass,
                                                   Collection<XmlModelClassProperty> nonStringPropertiesWithMaskTag) {
        super(join("For properties of a type different from String, the mask attribute is set. Error in properties",
                collectClassProperties(nonStringPropertiesWithMaskTag), "класса", modelClass.getName()),
            join("It is necessary to delete the mask attribute for these properties, or change their type to String"));
    }
}
