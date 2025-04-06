package com.sbt.model.index.exception;

import com.sbt.model.exception.parent.CheckModelException;

public class NameTooLongException extends CheckModelException {

    public NameTooLongException(String what, String name, int length, String solution) {
        super(String.format("%s %s exceeds the maximum length of %s characters.", what, name, length),
            solution);
    }
}
