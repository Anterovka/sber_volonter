package com.sbt.model.index.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.usermodel.UserXmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

public class IncompatibleAttributesWithMappedByException extends CheckXmlModelException {
    public IncompatibleAttributesWithMappedByException(XmlModelClassProperty property) {
        super(join("On attribute", property.getName(), "with mappedBy characteristic of class", property.getModelClass().getName(),
                "properties cannot be defined", UserXmlModelClassProperty.INDEX_TAG, "and/or", UserXmlModelClassProperty.UNIQUE_TAG),
            join("property or attribute", UserXmlModelClassProperty.MAPPED_BY_TAG));
    }
}
