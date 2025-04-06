package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class ObjectWithNoIdException extends DictionaryCheckParentException {

    public ObjectWithNoIdException(String entity) {
        super(join("For reference", entity, "an object without specifying the field 'id' is found."),
            "Each directory should have an identifier.");
    }
}
