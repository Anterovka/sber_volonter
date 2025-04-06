package com.sbt.model.cci.exceptions;

import com.sbt.model.exception.parent.CheckModelException;

/**
 * Exception: no field is indicated in the index.
 */
public class CciEmbeddedFieldException extends CheckModelException {

    public CciEmbeddedFieldException(String className, String typeName, String propertyName) {
        super(join("In the class", className, "an interzonal (cci) index is found, which includes a link",
                propertyName, "on the nested (embedded) type", typeName, "."),
            "Specify a particular field of the nested object in the index composition.");
    }
}
