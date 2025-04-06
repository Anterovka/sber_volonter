package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.util.Objects;

public class BooleanChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;

    public BooleanChecker(String id, String fieldName, String type) {
        this.id = id;
        this.fieldName = fieldName;
        this.type = type;
    }

    @Override
    public void check(Object value) {
        if (Objects.isNull(value)) {
            return;
        }

        checkValue(value);
    }

    private void checkValue(Object value) {
        final String stringValue = value.toString();
        if (Objects.equals("false", stringValue) ||
            Objects.equals("true", stringValue)) {
            return;
        }
        throw new DictionaryDataException(id, fieldName, value, type,
            "The error occurred while parsing the boolean value:the field value can only be either \"false\" or \"true\".");
    }

}
