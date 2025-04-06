package com.sbt.mg.exception.checkmodel;

public class ImplementationPropertyNotFoundException extends CheckXmlModelException {
    public ImplementationPropertyNotFoundException(String propertyName, String interfaceName, String implName) {
        super(join("Property", propertyName, "from the interface", interfaceName,
                "not found on class", implName, "implementing interface."),
            "The text should be declared as a property on the class, or it can be removed from the interface.");
    }
}
