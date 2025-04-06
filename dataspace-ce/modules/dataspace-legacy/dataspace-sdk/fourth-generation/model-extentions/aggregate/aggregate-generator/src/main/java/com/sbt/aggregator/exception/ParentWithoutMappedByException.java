package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class ParentWithoutMappedByException extends CheckXmlModelException {
    public ParentWithoutMappedByException(XmlModelClassProperty property) {
        super(join(propertyInCLass("о", property), "defined with the parent=\"true\".",
                "When parent = \"true\", the class requires a property with an attribute", property.getType(),
                XmlModelClassProperty.MAPPED_BY_TAG, "to property", property.getName()),
            join("Define a new binding property in the class", property.getType()));
    }
}
