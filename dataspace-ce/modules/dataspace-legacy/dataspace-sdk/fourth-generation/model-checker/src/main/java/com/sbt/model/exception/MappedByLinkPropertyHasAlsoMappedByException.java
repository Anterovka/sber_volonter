package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class MappedByLinkPropertyHasAlsoMappedByException extends CheckXmlModelException {
    public MappedByLinkPropertyHasAlsoMappedByException(XmlModelClassProperty property) {
        super(join("It is forbidden to define a mappedBy property that references a property with the mappedBy attribute.",
                "Error in determining properties", property.getName(), "and", property.getMappedBy(), "classes",
                property.getModelClass().getName(), "and", property.getType(), "respectively"),
            join("Perhaps, a new property should be created or it is necessary to abandon mappedBy"));
    }
}
