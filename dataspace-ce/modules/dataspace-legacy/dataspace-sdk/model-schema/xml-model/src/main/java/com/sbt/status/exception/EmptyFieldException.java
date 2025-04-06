package com.sbt.status.exception;

/**
 * Exception: Property not specified. Empty property.
 */
public class EmptyFieldException extends XmlStatusException {

    public EmptyFieldException(String tagName) {
        super(join("For status(status) no required property '", tagName, "' was set"),
            "It is necessary to set and initialize it according to the documentation requirements.");
    }
}
