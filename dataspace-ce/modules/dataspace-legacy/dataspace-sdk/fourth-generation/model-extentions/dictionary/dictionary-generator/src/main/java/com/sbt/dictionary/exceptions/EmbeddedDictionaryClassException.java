package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.Set;

public class EmbeddedDictionaryClassException extends DictionaryCheckParentException {

    public EmbeddedDictionaryClassException(Set<String> classNames) {
        super(join("Embedded class is not determined by the reference class. For classes", classNames.toString(),
                "simultaneously set the indicator of the reference book and the embedded class."),
            "If you need an embedded class, then remove the dictionary flag.");
    }
}
