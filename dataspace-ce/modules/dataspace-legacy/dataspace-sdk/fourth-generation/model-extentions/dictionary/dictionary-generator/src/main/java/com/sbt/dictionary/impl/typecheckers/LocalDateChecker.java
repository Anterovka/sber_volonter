package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateChecker extends TypeChecker {

    private static final String pattern = "yyyy-MM-dd";

    private final String id;
    private final String fieldName;
    private final String type;

    public LocalDateChecker(String id, String fieldName, String type) {
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
            LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException ex) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("Error during parsing of localDate value: value cannot be empty and " +
                    "must match the format \"%s\"", pattern));
        }
    }

}
