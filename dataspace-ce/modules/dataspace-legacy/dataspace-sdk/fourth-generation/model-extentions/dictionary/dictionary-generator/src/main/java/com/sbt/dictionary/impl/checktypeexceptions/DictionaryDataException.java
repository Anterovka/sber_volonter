package com.sbt.dictionary.impl.checktypeexceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DictionaryDataException extends DictionaryCheckParentException {

    public DictionaryDataException(String id, String fieldName, Object value, String type, String message) {
        super(String.format("The value of the field named \"%s\" with id=\"%s\" of type \"%s\" did not pass the check. %s",
                value, fieldName, id, type, message),
            "Please correct the comments.");
    }
}
