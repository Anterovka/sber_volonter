package com.sbt.model.index.exception;

import com.sbt.model.exception.parent.CheckModelException;

/**
 * Exception: no field is indicated in the index.
 */
public class EmptyIndexException extends CheckModelException {

    public EmptyIndexException(String className) {
        super(String.format("In class %s empty index was found.", className),
            "In the index, you need to specify at least one field.");
    }
}
