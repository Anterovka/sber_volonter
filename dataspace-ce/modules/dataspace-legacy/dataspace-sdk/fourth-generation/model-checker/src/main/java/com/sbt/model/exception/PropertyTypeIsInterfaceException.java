package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

/**
 * Exception: type specified as interface
 */
public class PropertyTypeIsInterfaceException extends CheckXmlModelException {
    /**
     * @param property the property with type violation
     */
    public PropertyTypeIsInterfaceException(XmlModelClassProperty property) {
        super(join("Property type", property.getName(), "(", property.getType(), ") of class",
                property.getModelClass().getName(), "is an interface"),
            "Change the type of external reference to interface implementation, or, if it is an external class, " +
                "rename the type.");
    }
}
