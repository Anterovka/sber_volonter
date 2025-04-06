package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Objects;

public class StringChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;
    private final XmlModelClassProperty property;

    public StringChecker(String id, String fieldName, String type, XmlModelClassProperty property) {
        this.id = id;
        this.fieldName = fieldName;
        this.type = type;
        this.property = property;
    }

    @Override
    public void check(Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        checkLength(value);
    }

    private void checkLength(Object value) {
        final int length = value.toString().length();
        if (length > getPropertyLength(property)) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("The string length exceeds the maximum possible (%s > %s)", length, property.getLength())
            );
        }
    }

}
