package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelInterfaceReference;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

public class ReferenceTypeRuleException extends FiledTypeRuleException {
    private ReferenceTypeRuleException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static ReferenceTypeRuleException of(XmlModelClassReference ref) {
        return new ReferenceTypeRuleException(
                EntityFieldType.REFERENCE,
                ref.getName(),
                EntityType.CLASS,
                ref.getModelClass().getName()
        );
    }

    public static ReferenceTypeRuleException of(XmlModelInterfaceReference ref) {
        return new ReferenceTypeRuleException(
                EntityFieldType.REFERENCE,
                ref.getName(),
                EntityType.INTERFACE,
                ref.getModelInterface().getName()
        );
    }
}
