package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.Set;

public class ExplicitFalseDictionaryOnHeirException extends DictionaryCheckParentException {

    public ExplicitFalseDictionaryOnHeirException(Set<String> falseDictionaryHeirClasses) {
        super(join("The model contains classes that are descendants of the reference dictionary (isDictionary = \"true\"), which explicitly",
                "the non-reference flag is set (isDictionary=\"false\"). All heirs of the directory are implicit",
                "are reference books. An explicit indication to the contrary is an error. Classes:", falseDictionaryHeirClasses.toString()),
            "Remove the non-directory attribute from the specified classes.");
    }
}
