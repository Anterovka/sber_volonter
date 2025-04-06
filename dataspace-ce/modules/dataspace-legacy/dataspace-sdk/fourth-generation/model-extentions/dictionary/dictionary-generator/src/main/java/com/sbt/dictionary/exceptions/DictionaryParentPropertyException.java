package com.sbt.dictionary.exceptions;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.model.dictionary.exceptions.DictionaryCheckParentException;

import java.util.List;
import java.util.stream.Collectors;

public class DictionaryParentPropertyException extends DictionaryCheckParentException {

    public DictionaryParentPropertyException(List<XmlModelClassProperty> properties) {
        super(join("Reference data cannot be aggregates or part of an aggregate.",
                "Installation of parent was detected in the fields:\n",
                formatProperties(properties)),
            "Remove the parent attribute from the listed fields."
        );
    }

    private static String formatProperties(List<XmlModelClassProperty> properties) {
        return properties.stream()
            .map(property -> "field" + property.getName() + " in class" + property.getModelClass().getName())
            .collect(Collectors.joining("\n"));
    }
}
