package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelInterface;

/**
 * Exception: The property name is already defined
 */
public class PropertyNotFoundInInterfaceException extends CheckXmlModelException {
    /**
     * @param propertyName Property name
     */
    public PropertyNotFoundInInterfaceException(String propertyName, XmlModelInterface iface) {
        super(join("Property with name", propertyName, "not found in interface",
                iface.getName()),
            join("Specify the correct interface that has the property", propertyName,
                "or specify your own property from the list", collectInterfaceProperties(iface.getPropertiesAsList())));
    }
}
