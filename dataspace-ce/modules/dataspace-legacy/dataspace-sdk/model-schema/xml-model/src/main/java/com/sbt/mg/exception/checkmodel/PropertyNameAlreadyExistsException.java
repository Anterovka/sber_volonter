package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;

/**
 * Exception: The property name is already defined
 */
public class PropertyNameAlreadyExistsException extends CheckXmlModelException {
    /**
     * @param property Name of the property
     */
    public PropertyNameAlreadyExistsException(XmlModelClassProperty property, XmlModelClass modelClass) {
        super(join("Property with name", property.getName(), "is already defined in class",
                modelClass.getName()),
            "The property may be reserved by the system (see the documentation). Name the property differently");
    }
}
