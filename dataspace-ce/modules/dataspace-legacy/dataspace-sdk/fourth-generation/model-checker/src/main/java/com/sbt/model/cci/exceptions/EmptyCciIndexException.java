package com.sbt.model.cci.exceptions;

import com.sbt.model.exception.parent.CheckModelException;

/**
 * Exception: no field is indicated in the index.
 */
public class EmptyCciIndexException extends CheckModelException {

    public EmptyCciIndexException(String className) {
        super(join("In the class", className, "an empty interzonal (cci) index is found."),
            "In the inter-zone index, it is necessary to specify at least one field.");
    }
}
