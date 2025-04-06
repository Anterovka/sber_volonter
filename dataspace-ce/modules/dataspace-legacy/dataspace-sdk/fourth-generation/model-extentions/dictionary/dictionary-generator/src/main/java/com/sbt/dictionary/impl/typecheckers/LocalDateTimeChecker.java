package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalDateTimeChecker extends TypeChecker {

    private static final String[] patterns = new String[] {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"};

    private final String id;
    private final String fieldName;
    private final String type;

    public LocalDateTimeChecker(String id, String fieldName, String type) {
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
            AtomicBoolean ok = new AtomicBoolean(false);
            for (String pattern : patterns) {
                try {
                    LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern(pattern));
                    ok.set(true);
                    break;
                } catch (DateTimeParseException ex) {
                    // NOPE
                }
            }
        if (!ok.get()) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("Error during parsing of localDateTime value: value cannot be empty and " +
                    "must match the formats \"%s\"", Arrays.toString(patterns)));
        }
    }

}
