package com.sbt.model.exception;

import com.sbt.model.exception.parent.CheckModelException;

import java.util.List;

/**
 * Exception: The index name is already set in the model
 */
public class IndexOnMappedByPropertyException extends CheckModelException {

    public IndexOnMappedByPropertyException(List<String> foundErrors) {
        super(join("Detected indexes that include mappedBy fields. These fields are physically. Errors:" +
                foundErrors.toString()),
            "The fields must be removed from the index composition.");
    }
}
