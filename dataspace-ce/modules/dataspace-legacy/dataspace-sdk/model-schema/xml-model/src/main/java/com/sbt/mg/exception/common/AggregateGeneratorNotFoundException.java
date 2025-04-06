package com.sbt.mg.exception.common;

import com.sbt.mg.exception.AnyPositionException;

public class AggregateGeneratorNotFoundException extends AnyPositionException {
    public AggregateGeneratorNotFoundException(String functionality) {
        super(join("For operation of process \"", functionality, "\" it is necessary that aggregate creation phase is completed"),
            join("make sure that your code is executed after the model aggregates are created"));
    }
}
