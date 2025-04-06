package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Collection;

public class TooMuchParentPropertiesException extends AggregateException {

    public TooMuchParentPropertiesException(String className, Collection<XmlModelClassProperty> parentProperties) {
        super(join("It is allowed to define no more than two properties with the parent = \"true\" sign on the class.",
                "The error was found in the properties of the", collectClassProperties(parentProperties), "class", className, "."),
            join("Reduce properties with the parent = \"true\" attribute to two"));
    }
}
