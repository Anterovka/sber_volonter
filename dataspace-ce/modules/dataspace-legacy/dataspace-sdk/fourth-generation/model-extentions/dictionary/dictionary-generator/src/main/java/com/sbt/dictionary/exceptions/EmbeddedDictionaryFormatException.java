package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class EmbeddedDictionaryFormatException extends DictionaryCheckParentException {

    public EmbeddedDictionaryFormatException(String propertyName, String className) {
        super(join("For embedded property", propertyName, "of class", className, "failed to transform data."),
            "Check the compliance of the JSON format with the structure of the model class.");
    }
}
