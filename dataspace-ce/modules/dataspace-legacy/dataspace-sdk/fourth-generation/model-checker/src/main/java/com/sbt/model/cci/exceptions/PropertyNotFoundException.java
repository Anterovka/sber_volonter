package com.sbt.model.cci.exceptions;

import com.sbt.model.cci.ModelCciLogic;
import com.sbt.model.exception.parent.CheckModelException;

public class PropertyNotFoundException extends CheckModelException {

    public PropertyNotFoundException(String propertyName, String cciIndexName, String modelClassname) {
        super(join("Suitable property",
                "'" + propertyName + "'",
                "specified in the index",
                cciIndexName == null ? "" : ("'" + cciIndexName + "'"),
                "class",
                "'" + modelClassname + "'",
                "не найдено."),
            join("Make sure that the property name is specified correctly.",
                "The property is either embedded or the type is included in",
                ModelCciLogic.CCI_SUITABLE_TYPES.toString() + ".",
                "Property cannot be a collection."));
    }
}
