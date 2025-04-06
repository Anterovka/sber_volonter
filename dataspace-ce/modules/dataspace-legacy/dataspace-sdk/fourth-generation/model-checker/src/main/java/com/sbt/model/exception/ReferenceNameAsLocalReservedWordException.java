package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelInterfaceReference;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
* Exception: The link name is reserved for the needs of the SDK.
 */
public class ReferenceNameAsLocalReservedWordException extends FieldNameAsLocalReservedWordException {
    private ReferenceNameAsLocalReservedWordException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static ReferenceNameAsLocalReservedWordException of(XmlModelClassReference ref) {
        return new ReferenceNameAsLocalReservedWordException(
                EntityFieldType.REFERENCE,
                ref.getName(),
                EntityType.CLASS,
                ref.getModelClass().getName()
        );
    }

    public static ReferenceNameAsLocalReservedWordException of(XmlModelInterfaceReference ref) {
        return new ReferenceNameAsLocalReservedWordException(
                EntityFieldType.REFERENCE,
                ref.getName(),
                EntityType.INTERFACE,
                ref.getModelInterface().getName()
        );
    }
}
