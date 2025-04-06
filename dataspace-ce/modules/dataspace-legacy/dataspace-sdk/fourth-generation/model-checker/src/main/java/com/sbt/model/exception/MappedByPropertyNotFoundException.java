package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class MappedByPropertyNotFoundException extends CheckXmlModelException {
    public MappedByPropertyNotFoundException(XmlModelClassProperty propertyWithMappedBy) {
        super(join("On the property", propertyWithMappedBy.getName(), "in the class", propertyWithMappedBy.getModelClass().getName(),
                "defined attribute", XmlModelClassProperty.MAPPED_BY_TAG, ". However in the class", propertyWithMappedBy.getType(),
                "the property with the name", propertyWithMappedBy.getMappedBy()),
            join("Check the correctness of filling the property", XmlModelClassProperty.MAPPED_BY_TAG));
    }
}
