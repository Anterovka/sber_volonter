package com.sbt.dictionary.exceptions;

import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

public class MandatoryFieldEmptyException extends DictionaryCheckParentException {

    public MandatoryFieldEmptyException(String entity, String field) {
        super(join("For entity", entity, "an object without specifying the required field", field),
            "Все поля, помеченные как обязательные (mandatory) в модели, должны быть указаны в данных.");
    }
}
