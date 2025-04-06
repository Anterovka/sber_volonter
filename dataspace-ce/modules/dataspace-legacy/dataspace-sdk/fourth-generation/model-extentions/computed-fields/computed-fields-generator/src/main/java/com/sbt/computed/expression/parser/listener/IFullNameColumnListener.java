package com.sbt.computed.expression.parser.listener;

import com.sbt.computed.expression.parser.CheckExpression;
import com.sbt.computed.expression.parser.ExpressionParserListener;
import java.util.ArrayList;
import java.util.List;

public interface IFullNameColumnListener  extends ExpressionParserListener {
    default  List<CheckExpression.PropertyLocation> getListForReplace(){
        return new ArrayList<>();
    }
}
