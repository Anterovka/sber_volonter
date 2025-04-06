package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The property name is reserved for the needs of the SDK
 */
public class FieldNameAsLocalReservedWordException extends CheckXmlModelException {
    private static String fieldTypeToStr(EntityFieldType fieldType) {
        switch (fieldType) {
            case PROPERTY:
                return "property";
            case REFERENCE:
                return "external reference";
            default:
                return fieldType.name();
        }
    }

    private static String entityTypeToStr(EntityType entityType) {
        switch (entityType) {
            case CLASS:
                return "of class";
            case INTERFACE:
                return "interface";
            default:
                return entityType.name();
        }
    }

    protected FieldNameAsLocalReservedWordException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        this(fieldTypeToStr(fieldType), fieldName, entityTypeToStr(entityType), entityName);
    }

    protected FieldNameAsLocalReservedWordException(String fieldType, String fieldName, String entityType, String entityName) {
        super(join("Name", fieldType, fieldName, entityType, entityName, "reserved for SDK needs."),
            "Correct according to requirements. The list of reserved words is described in the documentation.");
    }
}
