package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.List;

public class DictionaryPropertyDeleteException extends DictionaryCheckParentException {

    public DictionaryPropertyDeleteException(String entity, List<String> fields) {
        super(join("Deletion of fields", fields, "reference class", entity),
            "The deletion of reference class fields is prohibited. Return the deleted fields.");
    }
}
