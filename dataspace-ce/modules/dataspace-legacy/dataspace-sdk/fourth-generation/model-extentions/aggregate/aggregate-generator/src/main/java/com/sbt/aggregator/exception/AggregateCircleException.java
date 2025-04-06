package com.sbt.aggregator.exception;

import java.util.Set;

public class AggregateCircleException extends AggregateException {

    public AggregateCircleException(Set<String> registeredAggregates) {
        super(join("The structure of model aggregates is built according to the properties of the class with the parent = \"true\" attribute and should be a tree.",
                "Detected class loop", registeredAggregates),
            join("It is necessary to eliminate the cycle by determining which class should remove the parent = \"true\" attribute"));
    }
}
