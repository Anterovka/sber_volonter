package com.sbt.mg.exception.checkmodel;

import com.sbt.mg.exception.AnyPositionException;

public class IncorrectRootTagException extends AnyPositionException {
    public IncorrectRootTagException(String incorrectTagName, String correctTagName) {
        super(join("When reading the model, an incorrect root tag name was found -", incorrectTagName),
            join("Fix the name of the tag to", correctTagName));
    }
}
