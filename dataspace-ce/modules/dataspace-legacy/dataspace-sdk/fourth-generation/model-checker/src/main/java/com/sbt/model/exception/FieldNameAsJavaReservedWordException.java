package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
* Exception: The name of the property is a reserved word in the Java language
 */
public class FieldNameAsJavaReservedWordException extends CheckXmlModelException {
    private static String fieldTypeToStr(EntityFieldType fieldType) {
        switch (fieldType) {
            case PROPERTY: return "property";
            case REFERENCE: return "external reference";
            default: return fieldType.name();
        }
    }

    private static String entityTypeToStr(EntityType entityType) {
        switch (entityType) {
            case CLASS: return "of class";
            case INTERFACE: return "interface";
            default: return entityType.name();
        }
    }

    protected FieldNameAsJavaReservedWordException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        this(fieldTypeToStr(fieldType), fieldName, entityTypeToStr(entityType), entityName);
    }

    protected FieldNameAsJavaReservedWordException(String fieldType, String fieldName, String entityType, String entityName) {
        super(join("Name", fieldType, fieldName, entityType, entityName,
                "is a reserved word in the Java language."),
            "Fix according to requirements. The list of reserved words is described in the specification for Java.");
    }
}
