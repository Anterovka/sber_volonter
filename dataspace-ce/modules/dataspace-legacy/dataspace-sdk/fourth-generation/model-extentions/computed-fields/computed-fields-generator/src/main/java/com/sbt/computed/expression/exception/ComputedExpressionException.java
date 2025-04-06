package com.sbt.computed.expression.exception;

import com.sbt.mg.exception.GeneralSdkException;

public class ComputedExpressionException extends GeneralSdkException {

    public ComputedExpressionException() {
        super();
    }

    public ComputedExpressionException(String errorText, String solution) {
        super(errorText, solution);
    }

    @Override
    public String getPosition() {
        return "checking the computed expression in model.xml";
    }
}
