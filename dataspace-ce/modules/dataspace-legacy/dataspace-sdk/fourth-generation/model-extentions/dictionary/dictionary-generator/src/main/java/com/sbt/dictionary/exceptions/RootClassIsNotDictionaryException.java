package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class RootClassIsNotDictionaryException extends DictionaryCheckParentException {

    public RootClassIsNotDictionaryException(String className) {
        super(join("Class", className, "is a base class for the directory class, but is not a directory."),
            join("Set the flag is-dictionary on all base classes,",
                "либо уберите наследование от non-справочника."));
    }
}
