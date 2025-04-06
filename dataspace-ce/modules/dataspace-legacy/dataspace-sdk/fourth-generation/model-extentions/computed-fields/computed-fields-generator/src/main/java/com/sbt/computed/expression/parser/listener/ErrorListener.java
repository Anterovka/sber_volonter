package com.sbt.computed.expression.parser.listener;

import com.sbt.computed.expression.exception.ParserExpressionException;
import com.sbt.computed.expression.parser.ExpressionParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Interval;

public class ErrorListener extends BaseErrorListener {
    public final static ErrorListener INSTANCE = new ErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {

        String sourceName = recognizer.getInputStream().getSourceName();
        if (!sourceName.isEmpty()) {
            sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
        }
        String expression
                = ((ExpressionParser) recognizer).getTokenStream().getText(Interval.of(0, ((ExpressionParser) recognizer).getTokenStream().size()));
        throw new ParserExpressionException(expression, sourceName, line, charPositionInLine, msg);
    }
}

