package com.sbt.aggregator.exception;

import com.sbt.mg.data.model.XmlModelClass;

public class AggregatePositionNotFoundException extends AggregateException {
    public AggregatePositionNotFoundException(XmlModelClass modelClass) {
        super(join("For class", modelClass.getName(), "aggregate tree position is not found."),
            "Contact the developers.");
    }
}
