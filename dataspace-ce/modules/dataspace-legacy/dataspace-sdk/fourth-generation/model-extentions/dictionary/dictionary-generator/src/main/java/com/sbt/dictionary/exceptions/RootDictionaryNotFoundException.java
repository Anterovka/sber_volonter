package com.sbt.dictionary.exceptions;

public class RootDictionaryNotFoundException extends RuntimeException {

    public RootDictionaryNotFoundException() {
        super("The root class of the reference books RootEntity was not found.");
    }
}
