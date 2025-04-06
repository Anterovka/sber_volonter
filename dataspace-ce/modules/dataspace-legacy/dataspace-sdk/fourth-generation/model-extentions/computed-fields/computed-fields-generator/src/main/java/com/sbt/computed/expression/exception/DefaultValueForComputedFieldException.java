package com.sbt.computed.expression.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class DefaultValueForComputedFieldException extends ComputedExpressionException {
    public DefaultValueForComputedFieldException(XmlModelClassProperty property) {
        super(String.format("The default value for the computed field %s.%s is not allowed.", property.getModelClass().getName())
            , "The computed value or default value must be deleted.");
    }
}

