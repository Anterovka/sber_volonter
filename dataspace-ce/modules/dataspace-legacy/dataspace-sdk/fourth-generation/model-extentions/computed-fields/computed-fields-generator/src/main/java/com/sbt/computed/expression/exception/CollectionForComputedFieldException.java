package com.sbt.computed.expression.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;

public class CollectionForComputedFieldException extends ComputedExpressionException {
    public CollectionForComputedFieldException(XmlModelClassProperty property) {
        super(join("Computed field ", property.getModelClass().getName(), ".", property.getName(), " cannot participate in relations between classes.")
            , "The attribute mappedBy must be deleted, or the computed expression must be removed.");
    }
}
