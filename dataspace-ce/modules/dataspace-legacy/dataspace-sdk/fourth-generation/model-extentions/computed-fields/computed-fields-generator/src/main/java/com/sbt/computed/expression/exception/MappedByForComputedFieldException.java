package com.sbt.computed.expression.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class MappedByForComputedFieldException extends ComputedExpressionException {
    public MappedByForComputedFieldException(XmlModelClassProperty property) {
        super(join("Computed field ", property.getModelClass().getName(), ".", property.getName(), " cannot participate in relations between classes.")
            , "You need to remove the mappedBy attribute or remove the computed expression.");
    }
}

