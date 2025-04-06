package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.util.Objects;

public class CharacterChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;

    public CharacterChecker(String id, String fieldName, String type) {
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
        final char[] chars = stringValue.toCharArray();
        if (chars.length != 1) {
            throw new DictionaryDataException(id, fieldName, value, type,
                "Error during parsing of character value: field value cannot be empty or consist of multiple characters.");
        }
    }

}
