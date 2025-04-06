package com.sbt.mg.exception.checkmodel;

public class ImplementationPropertyCollectionException extends CheckXmlModelException {
    public ImplementationPropertyCollectionException(String propertyName, String interfaceName, String implName) {
        super(join("Property", propertyName, "has a different parameter collection on the interface", interfaceName,
                "and on implementation", implName),
            "The parameter collection of the property must be consistent in the interface and implementation.");
    }
}
