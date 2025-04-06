package com.sbt.model.exception;

import com.sbt.mg.data.model.Property;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.Collection;

public class IdempotenceExcludeSectionWrongPropertiesException extends CheckXmlModelException {
    public IdempotenceExcludeSectionWrongPropertiesException(XmlModelClass modelClass,
                                                             Collection<Property> wrongProperties) {
        super(join("In the idempotence-exclude section of the", modelClass.getName(), "class, properties that are not present in the class are listed:",
                collectProperties(wrongProperties, Property::getName, ", ")),
            join("It is necessary to delete the specified properties from the idempotence-exclude section, or correct their names to existing ones"));
    }
}
