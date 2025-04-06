package com.sbt.computed.expression.exception;

public class ComputedArgumentParserExpressionException extends ComputedExpressionException {
    public ComputedArgumentParserExpressionException(String expression, String propertyName) {
        super(String.format("In the expression %s  the computed property %s is specified."
            , "\"" + expression + "\"", propertyName), "Using as arguments computed expression " +
            "computed fields are not allowed.");
    }
}

