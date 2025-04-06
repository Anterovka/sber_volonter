package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DictionaryConsistencyException extends DictionaryCheckParentException {

    public DictionaryConsistencyException(String checkedEntity, String field, String fieldType, String id) {
        super(join("For type", checkedEntity, "no type value was found", fieldType, "for field",
                field, "с id", id),
            "It is necessary to establish correct links between reference books."
        );
    }
}
