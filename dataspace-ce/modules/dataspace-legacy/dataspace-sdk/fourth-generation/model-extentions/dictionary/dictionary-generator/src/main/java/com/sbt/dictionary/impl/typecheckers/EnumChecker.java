package com.sbt.dictionary.impl.typecheckers;

import com.sbt.dictionary.impl.checktypeexceptions.DictionaryDataException;
import com.sbt.mg.data.model.XmlEnumValue;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnumChecker extends TypeChecker {

    private final String id;
    private final String fieldName;
    private final String type;
    private final XmlModelClassProperty property;

    public EnumChecker(String id, String fieldName, String type, XmlModelClassProperty property) {
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

        if (value.toString().isEmpty()) {
            throw new DictionaryDataException(id, fieldName, value, type,
                "The value cannot be empty.");
        }

        final Optional<XmlModelClassEnum> enumOpt = property.getModelClass().getModel().getEnums().stream()
                .filter(en -> en.getName().equals(property.getType()))
                .findFirst();

        if (!enumOpt.isPresent()) {
            throw new DictionaryDataException(id, fieldName, value, type,
                "The model does not find an enumeration. Please show this error to developers.");
        }

        final XmlModelClassEnum anEnum = enumOpt.get();
        final Optional<XmlEnumValue> valueOpt = anEnum.getEnumValues().stream()
                .filter(en -> en.getName().equals(value.toString()))
                .findFirst();
        if (!valueOpt.isPresent()) {
            throw new DictionaryDataException(id, fieldName, value, type,
                String.format("The enumeration value can be only from the declared values: %s.",
                    anEnum.getEnumValues().stream().map(XmlEnumValue::getName).collect(Collectors.toSet())));
        }
    }
}
