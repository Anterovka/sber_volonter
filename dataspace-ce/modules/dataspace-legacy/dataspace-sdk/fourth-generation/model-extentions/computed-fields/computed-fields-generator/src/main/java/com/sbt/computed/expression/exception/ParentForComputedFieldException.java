package com.sbt.computed.expression.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class ParentForComputedFieldException extends ComputedExpressionException {

    public ParentForComputedFieldException(XmlModelClassProperty property) {
        super(join("Computed field ", property.getModelClass().getName(), ".", property.getName(), " cannot participate in relations between classes.")
            , "The attribute parent should be deleted, or the computed expression should be removed.");
    }
}

