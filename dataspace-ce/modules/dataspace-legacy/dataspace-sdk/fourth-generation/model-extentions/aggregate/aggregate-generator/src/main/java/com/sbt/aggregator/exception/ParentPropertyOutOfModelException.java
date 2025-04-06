package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class ParentPropertyOutOfModelException extends AggregateException {

    public ParentPropertyOutOfModelException(XmlModelClassProperty property) {
        super(join("It is forbidden to mark properties with parent = \"true\" whose type is not described in the model.",
                "The error was found in the property", property, ". The type is not from the model:", property.getType()),
            join("Check the correctness of the type filling or remove the parent attribute"));
    }
}
