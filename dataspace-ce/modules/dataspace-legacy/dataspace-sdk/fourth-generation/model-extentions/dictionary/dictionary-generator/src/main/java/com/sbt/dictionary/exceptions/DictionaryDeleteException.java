package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DictionaryDeleteException extends DictionaryCheckParentException {

    public DictionaryDeleteException(String entity, Object id) {
        super(join("For reference", entity, "no object with id = '", id, "' was found in new data.",
                "The object is present in the old data."),
            "The deletion of reference data is prohibited.Return the deleted records.");
    }
}
