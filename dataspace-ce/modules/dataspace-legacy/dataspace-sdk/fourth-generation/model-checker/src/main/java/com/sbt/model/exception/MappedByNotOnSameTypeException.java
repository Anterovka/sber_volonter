package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class MappedByNotOnSameTypeException extends CheckXmlModelException {
    public MappedByNotOnSameTypeException(XmlModelClassProperty property) {
        super(join("It is forbidden to create properties with the mappedBy attribute, whose type does not correspond to the basic type.",
                "The error occurred in the class", property.getModelClass().getName(), "in the property", property.getName(),
                ", since the type of the property", property.getMappedBy(), "of the class", property.getType(), "is not equal to",
                property.getModelClass().getName()),
            join("Another property must be indicated as mappedBy or specify the type",
                property.getModelClass().getName(), "instead of", property.getType()));
    }
}
