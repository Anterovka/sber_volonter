package com.sbt.dictionary.exceptions;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.List;
import java.util.stream.Collectors;

public class NotSupportedOffsetPropertiesException extends DictionaryCheckParentException {

    public NotSupportedOffsetPropertiesException(List<XmlModelClassProperty> properties) {
        super(join("For reference books, the OffsetDateTime type is not supported.",
                "Found for properties:", collectProperties(properties)),
            "Use another type of dates. For example, LocalDateTime, with translation to UTC.");
    }

    private static List<String> collectProperties(List<XmlModelClassProperty> properties) {
        return properties.stream()
            .map(property -> propertyInCLass("о", property))
            .collect(Collectors.toList());
    }
}
