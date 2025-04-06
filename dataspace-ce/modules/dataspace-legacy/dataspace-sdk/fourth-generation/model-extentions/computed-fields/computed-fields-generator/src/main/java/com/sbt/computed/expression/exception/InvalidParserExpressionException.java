package com.sbt.computed.expression.exception;

public class InvalidParserExpressionException extends ComputedExpressionException {
    public InvalidParserExpressionException(String expression) {
        super("Error checking computed-property " + expression, "Check the correctness of the expression.");
    }
}

