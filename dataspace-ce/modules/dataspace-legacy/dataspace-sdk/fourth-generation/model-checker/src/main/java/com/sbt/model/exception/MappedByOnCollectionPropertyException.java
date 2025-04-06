package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class MappedByOnCollectionPropertyException extends CheckXmlModelException {
    public MappedByOnCollectionPropertyException(XmlModelClassProperty property) {
        super(join("It is forbidden to create properties with the mappedBy attribute referring to a property declared as a collection.",
                "The error occurred in the class", property.getModelClass().getName(), "in the property", property.getName()),
            join("It is necessary to specify another property as mappedBy or create a new linking property"));
    }
}
