package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class UniqueConstraintException extends DictionaryCheckParentException {

    public UniqueConstraintException(String entity, String propertyName, String value) {
        super(join("For field", propertyName, "entity", entity,
                "a violation of uniqueness was detected. The value ''", value, "' was provided"),
            "Make sure all fields marked as unique are unique.");
    }
}
