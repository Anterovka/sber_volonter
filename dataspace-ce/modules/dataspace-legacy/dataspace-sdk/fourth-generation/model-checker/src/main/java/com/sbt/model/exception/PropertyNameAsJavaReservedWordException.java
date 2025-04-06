package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The name of the property is a reserved word in the Java language
 */
public class PropertyNameAsJavaReservedWordException extends FieldNameAsJavaReservedWordException {
    private PropertyNameAsJavaReservedWordException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static PropertyNameAsJavaReservedWordException of(XmlModelClassProperty property) {
        return new PropertyNameAsJavaReservedWordException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.CLASS,
                property.getModelClass().getName()
        );
    }

    public static PropertyNameAsJavaReservedWordException of(XmlModelInterfaceProperty property) {
        return new PropertyNameAsJavaReservedWordException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.INTERFACE,
                property.getModelInterface().getName()
        );
    }
}
