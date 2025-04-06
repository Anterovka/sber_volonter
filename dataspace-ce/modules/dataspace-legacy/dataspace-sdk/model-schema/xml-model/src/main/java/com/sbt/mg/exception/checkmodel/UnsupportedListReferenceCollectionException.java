package com.sbt.mg.exception.checkmodel;

/**
 * Exception: Unsupported collection
 */
public class UnsupportedListReferenceCollectionException extends CheckXmlModelException {
    /**
     * @param className    Class name
     * @param propertyName Property name
     */
    public UnsupportedListReferenceCollectionException(String className, String propertyName) {
        super(join("Collection of links in the form of a List is not supported for these links. Error in the property", propertyName,
                "class", className),
            join("Измените тип list на set"));
    }
}
