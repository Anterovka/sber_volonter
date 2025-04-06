package com.sbt.mg.exception;

public class AnyPositionException extends GeneralSdkException {
    public AnyPositionException() {
        super();
    }

    public AnyPositionException(String message, String solution) {
        super(message, solution);
    }
}
