package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class MappedByLinkNotExistsInClassException extends CheckXmlModelException {
    public MappedByLinkNotExistsInClassException(XmlModelClassProperty property) {
        super(join("It is forbidden to define properties with the given mappedBy on an undeclared property.",
                "In the class, property.getModelClass().getName(), is defined a property named", property.getName(),
                "referencing the class", property.getType(), ", however the property named",
                property.getMappedBy(), ", specified in mappedBy, was not found.",
                "If you are using for communication the property with the tag", XmlModelClass.REFERENCE_TAG,
                ", then such operation is considered inadmissible"),
            join("Add a property to the class", property.getType(), "with name", property.getMappedBy(),
                "of type", property.getModelClass().getName(), ", or specify the correct association property."));
    }
}
