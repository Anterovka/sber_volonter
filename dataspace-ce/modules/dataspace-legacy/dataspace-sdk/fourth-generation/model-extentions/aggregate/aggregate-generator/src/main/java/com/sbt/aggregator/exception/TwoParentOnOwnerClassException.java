package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class TwoParentOnOwnerClassException extends AggregateException {
    public TwoParentOnOwnerClassException(String modelClass, XmlModelClassProperty firstProperty, XmlModelClassProperty secondProperty) {
        super(join("Two properties", firstProperty.getName(), "and", secondProperty.getName(), "of class", modelClass,
                "have a parent sign = \"true\".It is forbidden to define two properties with a parent sign of the same type", firstProperty.getType()),
            join("It is necessary to remove the parent attribute from one of the properties in the class", modelClass));
    }
}
