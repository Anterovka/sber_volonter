package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.util.Objects;

public class LongChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;

    public LongChecker(String id, String fieldName, String type) {
        this.id = id;
        this.fieldName = fieldName;
        this.type = type;
    }

    @Override
    public void check(Object value) {
        if (Objects.isNull(value)) {
            return;
        }

        checkParse(value);
    }

    private void checkParse(Object value) {
        try {
            Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("Error during parsing of long value: %s", ex.getMessage()));
        }
    }

}
