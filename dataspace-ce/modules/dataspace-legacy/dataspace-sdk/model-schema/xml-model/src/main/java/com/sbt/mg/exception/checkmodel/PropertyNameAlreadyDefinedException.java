package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.usermodel.UserXmlModelInterfaceProperty;

import java.util.List;

/**
 * Exception: The property name is already defined
 */
public class PropertyNameAlreadyDefinedException extends CheckXmlModelException {
    /**
     * @param property Name of the property
     */
    public PropertyNameAlreadyDefinedException(XmlModelClassProperty property, String className) {
        super(join("Property", property, "is already defined on the class", className),
            "Rename one of the properties or delete it");
    }

    public PropertyNameAlreadyDefinedException(UserXmlModelInterfaceProperty propertyName, String interfaceName) {
        super(join("Свойство", propertyName, "уже определено на интерфейсе", interfaceName),
            "Rename one of the properties or delete it");
    }

    public PropertyNameAlreadyDefinedException(String className, List<String> names) {
        super(join("Properties [", String.join(",", names), "] are duplicated on the class", className),
            "Rename one of the properties or delete it");
    }
}
