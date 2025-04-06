package com.sbt.model.utils.exception;

public class JsonValidationException extends RuntimeException {
    public JsonValidationException(Throwable cause) {
        super("Error during validation of the Json file. See details in cause.", cause);
    }
}
