package com.sbt.dictionary.exceptions;

import com.sbt.dictionary.DictObject;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.List;

public class DictionaryOrderException extends DictionaryCheckParentException {

    public DictionaryOrderException(List<DictObject> objects) {
        super(
            String.format(
                "Error in sorting reference types. Failed to establish load sequence for %s",
                objects),
            "Eliminate loops among the listed objects."
        );
    }
}
