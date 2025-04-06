package com.sbt.model.index.exception;

import com.sbt.model.exception.parent.CheckModelException;

public class TooLongValueException extends CheckModelException {

    public TooLongValueException(String what, String forWhat, String actualValues, int maxValue) {

        super(join("Value", what, "for", forWhat, "is too long (", actualValues, ")"),
            join("Reduce the length to", maxValue, "characters."));

    }
}
