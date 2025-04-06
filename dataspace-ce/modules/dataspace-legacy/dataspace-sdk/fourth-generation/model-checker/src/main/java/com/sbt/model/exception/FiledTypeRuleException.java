package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.CheckXmlModelException;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The class name is already set in the base module model
 */
public class FiledTypeRuleException extends CheckXmlModelException {
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

    protected FiledTypeRuleException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(join("Attribute \"", XmlModelClassProperty.TYPE_TAG, "\"", fieldTypeToStr(fieldType), fieldName,
                entityTypeToStr(entityType), entityName,
                "must start with an uppercase letter, not contain Cyrillic characters, and not be empty."),
            "Fix according to the requirements");
    }
}
