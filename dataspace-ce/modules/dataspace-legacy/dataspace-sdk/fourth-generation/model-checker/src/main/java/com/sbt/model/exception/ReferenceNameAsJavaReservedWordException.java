package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.XmlModelInterfaceReference;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The link name is a reserved word in the Java language
 */
public class ReferenceNameAsJavaReservedWordException extends FieldNameAsJavaReservedWordException {
    protected ReferenceNameAsJavaReservedWordException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static ReferenceNameAsJavaReservedWordException of (XmlModelClassReference ref) {
        return new ReferenceNameAsJavaReservedWordException(
                EntityFieldType.REFERENCE,
                ref.getName(),
                EntityType.CLASS,
                ref.getModelClass().getName()
        );
    }

    public static ReferenceNameAsJavaReservedWordException of (XmlModelInterfaceReference ref) {
        return new ReferenceNameAsJavaReservedWordException(
                EntityFieldType.REFERENCE,
                ref.getName(),
                EntityType.INTERFACE,
                ref.getModelInterface().getName()
        );
    }
}
