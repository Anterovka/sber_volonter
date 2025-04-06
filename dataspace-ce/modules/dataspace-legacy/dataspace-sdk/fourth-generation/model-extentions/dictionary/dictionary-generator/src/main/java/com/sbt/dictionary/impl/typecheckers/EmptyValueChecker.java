package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.util.Objects;

public class EmptyValueChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;

    public EmptyValueChecker(String id, String fieldName, String type) {
        this.id = id;
        this.fieldName = fieldName;
        this.type = type;
    }

    @Override
    public void check(Object value) {
        if (Objects.isNull(value)) {
            return;
        }

        if (value.toString().isEmpty()) {
            throw new DictionaryDataException(id, fieldName, value, type,
                "The value cannot be empty.");
        }
    }
}
