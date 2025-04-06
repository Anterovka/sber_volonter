package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class WrongIdCategoryException extends DictionaryCheckParentException {

    public WrongIdCategoryException(String entity) {
        super("For reference books, there can't be a category ID different from MANUAL.",
            join(" Fix it in the class", entity,
                "or remove it altogether - for reference books, the category will be set by default."));
    }
}
