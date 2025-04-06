package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.data.model.interfaces.ReferenceFromXml;

/**
 * Exception: The property name is already defined
 */
public class ReferenceNameAlreadyDefinedException extends CheckXmlModelException {
    /**
     * @param propertyName Property name
     */
    public ReferenceNameAlreadyDefinedException(ReferenceFromXml propertyName) {
        super(join("Reference name (attribute name)", propertyName, "is already defined on the class"));
    }
}
