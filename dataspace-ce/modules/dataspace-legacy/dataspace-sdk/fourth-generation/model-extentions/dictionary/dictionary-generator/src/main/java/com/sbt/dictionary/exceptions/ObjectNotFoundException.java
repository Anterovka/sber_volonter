package com.sbt.dictionary.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String type, Object id) {
        super(String.format("Error in reference type processing. The %s was not found: %s", type, id));
    }
}
