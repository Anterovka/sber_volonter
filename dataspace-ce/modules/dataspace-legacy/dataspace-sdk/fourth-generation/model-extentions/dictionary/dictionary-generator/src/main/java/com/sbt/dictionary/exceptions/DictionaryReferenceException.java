package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.List;

public class DictionaryReferenceException extends DictionaryCheckParentException {

    public DictionaryReferenceException(List<String> errors) {
        super(join("When checking reference books, errors were found: ", errors.toString()),
            "Eliminate errors."
        );
    }
}
