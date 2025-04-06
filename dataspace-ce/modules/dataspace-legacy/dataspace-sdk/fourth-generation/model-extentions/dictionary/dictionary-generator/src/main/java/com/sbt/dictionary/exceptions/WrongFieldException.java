package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.Set;

public class WrongFieldException extends DictionaryCheckParentException {

    public WrongFieldException(String entity, Set<String> wrongFields) {
        super(join("The described fields", wrongFields, "for the class", entity, "are not found in the class properties."),
            "The data for filling should contain only the fields declared in the model description (and not mappedBy fields).");
    }
}
