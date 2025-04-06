package com.sbt.mg.exception.common;

public class SystemClassNotFoundException extends RuntimeException {

    public SystemClassNotFoundException(String className) {
        super(String.format("The system class %s was not found.", className));
    }
}
