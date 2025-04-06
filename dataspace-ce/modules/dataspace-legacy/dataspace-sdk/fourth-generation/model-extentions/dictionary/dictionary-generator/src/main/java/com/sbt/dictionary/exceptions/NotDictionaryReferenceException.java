package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class NotDictionaryReferenceException extends DictionaryCheckParentException {

    public NotDictionaryReferenceException(String entity, String propertyClassName) {
        super(join("In the class", entity, "a reference to an object of type", propertyClassName,
                "which is not a directory"),
            join("Reference cannot refer to anything other than reference.",
                "Delete the link or add the reference flag to the class",
                propertyClassName));
    }
}
