package com.sbt.dictionary.exceptions;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.List;
import java.util.stream.Collectors;

public class NotSupportedCollectionPropertiesException extends DictionaryCheckParentException {

    public NotSupportedCollectionPropertiesException(List<XmlModelClassProperty> properties) {
        super(join("For reference books, only collections of primitives are supported.",
                "Properties", collectProperties(properties), "are not a collection of primitives."),
            "Remove the listed properties.");
    }

    private static List<String> collectProperties(List<XmlModelClassProperty> properties) {
        return properties.stream()
            .map(property -> propertyInCLass("о", property))
            .collect(Collectors.toList());
    }
}
