package com.sbt.mg.exception.checkmodel;

public class ImplementationPropertyTypeException extends CheckXmlModelException {
    public ImplementationPropertyTypeException(String propertyName, String interfaceName, String implName) {
        super(join("Property", propertyName, "has a different type on the interface", interfaceName,
                "and on implementation", implName),
            "The type of property must be consistent in the interface and implementation");
    }
}
