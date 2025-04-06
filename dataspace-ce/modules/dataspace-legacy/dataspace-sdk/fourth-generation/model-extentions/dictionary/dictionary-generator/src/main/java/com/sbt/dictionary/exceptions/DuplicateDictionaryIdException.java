package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DuplicateDictionaryIdException extends DictionaryCheckParentException {

    public DuplicateDictionaryIdException(String entity, String id) {
        super(join("Duplicates the Reference Object", entity, "with id='", id, "'"),
            "Identifiers of reference books should be unique.");
    }
}
