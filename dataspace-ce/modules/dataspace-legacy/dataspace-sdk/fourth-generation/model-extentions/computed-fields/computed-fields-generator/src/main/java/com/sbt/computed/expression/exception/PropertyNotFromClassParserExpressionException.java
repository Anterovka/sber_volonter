package com.sbt.computed.expression.exception;

public class PropertyNotFromClassParserExpressionException extends ComputedExpressionException {
    public PropertyNotFromClassParserExpressionException(String expression, String propertyName) {
        super(join("In the expression ", '"', expression, '"', "  the property ", propertyName,
            " not set in the current class."), "The arguments of the computed expression can only use" +
            " The properties of the current class.");

    }
}

