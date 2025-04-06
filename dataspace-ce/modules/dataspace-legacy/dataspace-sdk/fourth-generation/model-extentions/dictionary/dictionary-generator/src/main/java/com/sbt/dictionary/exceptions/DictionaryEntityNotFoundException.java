package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class DictionaryEntityNotFoundException extends DictionaryCheckParentException {

    public DictionaryEntityNotFoundException(String entity) {
        super(join("The model does not find the class reference", entity),
            "Each type of directory defined in the data must be reflected in the model description.");
    }
}
