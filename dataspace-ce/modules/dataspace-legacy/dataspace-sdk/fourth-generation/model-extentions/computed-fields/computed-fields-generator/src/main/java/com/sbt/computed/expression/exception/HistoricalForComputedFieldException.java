package com.sbt.computed.expression.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class HistoricalForComputedFieldException extends ComputedExpressionException {
    public HistoricalForComputedFieldException(XmlModelClassProperty property) {
        super(join("Historicization of computed field ", property.getModelClass().getName(), ".", property.getName(), " is not allowed.")
            , "You must set the historical=false attribute or delete the computed expression.");
    }
}

