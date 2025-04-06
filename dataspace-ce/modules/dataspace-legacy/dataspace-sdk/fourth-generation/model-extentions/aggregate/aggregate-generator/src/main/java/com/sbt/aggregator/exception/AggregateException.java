package com.sbt.aggregator.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class AggregateException extends GeneralSdkException {

    public AggregateException() {
        super();
    }

    public AggregateException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "Functionality: aggregates";
    }
}
