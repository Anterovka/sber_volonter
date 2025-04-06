package com.sbt.dictionary.impl.typecheckers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;

import java.util.Collection;
import java.util.Objects;

public class CollectionChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;
    private final TypeChecker elementChecker;

    private final ObjectMapper mapper = new ObjectMapper();

    public CollectionChecker(String id, String fieldName, String type, TypeChecker elementChecker) {
        this.id = id;
        this.fieldName = fieldName;
        this.type = type;
        this.elementChecker = elementChecker;
    }

    @Override
    public void check(Object value) {
        if (Objects.isNull(value)) {
            return;
        }

        checkArray(value);
    }

    private void checkArray(Object value) {
        if (!Collection.class.isAssignableFrom(value.getClass())) {
            throw new DictionaryDataException(id, fieldName, value, type,
                "The error occurred while parsing the collection: the value is not a collection.");
        }

        final Collection<?> collection = (Collection<?>) value;
        collection.forEach(this::checkElement);
    }

    private void checkElement(Object value) {
        this.elementChecker.check(value);
    }

}
