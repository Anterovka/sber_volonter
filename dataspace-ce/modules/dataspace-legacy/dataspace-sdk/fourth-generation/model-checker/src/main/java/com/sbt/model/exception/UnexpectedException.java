package com.sbt.model.exception;

import com.sbt.model.exception.parent.CheckModelException;

/**
 * Exception: no field is indicated in the index.
 */
public class UnexpectedException extends CheckModelException {


    public UnexpectedException(String whatHappen) {
        super(whatHappen, "Show the development error with attached model.xml and pdm.xml.");
    }
}
