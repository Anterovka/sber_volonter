package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class NoMappedByPropertyToParentException extends AggregateException {
    public NoMappedByPropertyToParentException(String className, XmlModelClassProperty property) {
        super(join("On class", className, "there is a property", property.getName(), "with a parent = \"true\" sign.",
                "However, on the class", property.getType(), "there is no property with the type", className,
                "and MappedBy = \"" + property.getName() + "\"."),
            join("A new property with the type \"" + className + "\" and mappedBy=\"" + property.getName() + "\" needs to be added"));
    }
}
