package com.sbt.computed.expression.exception;

public class ParserExpressionException extends ComputedExpressionException {
    public ParserExpressionException(String expression, String sourceName, int line, int charPositionInLine,
                                     String msg) {
        super("Error in calculated column function \"" + expression + "\"", sourceName + "line " + line + ':' + charPositionInLine + ' ' + msg);
    }
}

