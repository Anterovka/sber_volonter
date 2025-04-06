package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class CounterException extends DictionaryCheckParentException {

    public CounterException() {
        super("Error in sorting reference types.",
            "Show the error to developers. Attach the model and files with reference data.");
    }
}
