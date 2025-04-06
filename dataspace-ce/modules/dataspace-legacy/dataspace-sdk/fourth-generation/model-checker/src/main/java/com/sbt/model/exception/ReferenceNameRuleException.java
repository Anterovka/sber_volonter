package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelInterfaceReference;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The class name is already set in the base module model
 */
public class ReferenceNameRuleException extends FiledNameRuleException {
    protected ReferenceNameRuleException(EntityFieldType fieldType,
                                         String fieldName,
                                         EntityType entityType,
                                         String entityName,
                                         int maxNameLength) {
        super(fieldType, fieldName, entityType, entityName, maxNameLength);
    }

    public static ReferenceNameRuleException of(XmlModelClassReference reference, int maxNameLength) {
        return new ReferenceNameRuleException(
                EntityFieldType.REFERENCE,
                reference.getName(),
                EntityType.CLASS,
                reference.getModelClass().getName(),
                maxNameLength
        );
    }

    public static ReferenceNameRuleException of(XmlModelInterfaceReference reference, int maxNameLength) {
        return new ReferenceNameRuleException(
                EntityFieldType.REFERENCE,
                reference.getName(),
                EntityType.INTERFACE,
                reference.getModelInterface().getName(),
                maxNameLength
        );
    }
}
