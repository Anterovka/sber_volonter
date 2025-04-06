package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

import java.util.Collection;

public class ParentIsCollectionException extends AggregateException {
    public ParentIsCollectionException(String className, Collection<XmlModelClassProperty> parentCollectionProperties) {
        super(join("It is forbidden to combine the characteristics of collection and parent = \" true\" simultaneously.",
                "Error in properties", collectClassProperties(parentCollectionProperties),
                "class", className),
            join("Perhaps, each such property should be broken down into a pair of properties with only the collection, and only with the parent flag"));
    }
}
