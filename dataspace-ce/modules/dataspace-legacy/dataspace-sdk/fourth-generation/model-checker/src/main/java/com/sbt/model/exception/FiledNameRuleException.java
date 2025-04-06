package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The class name is already set in the base module model
 */
public class FiledNameRuleException extends CheckXmlModelException {
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

    protected FiledNameRuleException(EntityFieldType fieldType,
                                     String fieldName,
                                     EntityType entityType,
                                     String entityName,
                                     int maxNameLength) {
        this(fieldTypeToStr(fieldType), fieldName, entityTypeToStr(entityType), entityName, maxNameLength);
    }

    protected FiledNameRuleException(String fieldType,
                                     String fieldName,
                                     String entityType,
                                     String entityName,
                                     int maxNameLength) {
        super(join("Name", fieldType, " should start with lowercase letters, " +
                    "consist only of Latin characters, not be empty and not exceed the length",
                maxNameLength, "Error in name", fieldType, fieldName, entityType,
                entityName),
            "Correct according to the requirements.");
    }
}
