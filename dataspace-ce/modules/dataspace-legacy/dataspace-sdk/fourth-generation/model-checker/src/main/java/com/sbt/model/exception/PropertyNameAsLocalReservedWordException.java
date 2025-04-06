package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The property name is reserved for the needs of the SDK
 */
public class PropertyNameAsLocalReservedWordException extends FieldNameAsLocalReservedWordException {
    private PropertyNameAsLocalReservedWordException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static PropertyNameAsLocalReservedWordException of(XmlModelClassProperty property) {
        return new PropertyNameAsLocalReservedWordException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.CLASS,
                property.getModelClass().getName()
        );
    }

    public static PropertyNameAsLocalReservedWordException of(XmlModelInterfaceProperty property) {
        return new PropertyNameAsLocalReservedWordException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.INTERFACE,
                property.getModelInterface().getName()
        );
    }
}
