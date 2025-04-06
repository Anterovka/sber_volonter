package com.sbt.dictionary.exceptions;

public class DtoNotFoundException extends RuntimeException {

    public DtoNotFoundException(String type) {
        super(String.format("Error in reference type processing. The %s was not found", type));
    }
}
