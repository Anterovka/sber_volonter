package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DictionaryClassDeleteException extends DictionaryCheckParentException {

    public DictionaryClassDeleteException(String entity) {
        super(join("Deleting a reference class", entity),
            "The deletion of reference classes is prohibited.Return the deleted classes.");
    }
}
