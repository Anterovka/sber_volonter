package com.sbt.computed.expression.exception;

public class PropertyFromOtherClassInParserExpressionException extends ComputedExpressionException {
    public PropertyFromOtherClassInParserExpressionException(String expression, String className, String propertyName) {
        super(join("In the expression ", '"', expression, '"', " another class's field is referenced",
            className, '.', propertyName), "In the arguments of the computed expression, you can use only" +
            " the properties of the current class.");
    }
}

