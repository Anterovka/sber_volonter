package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The class name is already set in the base module model
 */
public class PropertyNameRuleException extends FiledNameRuleException {
    private PropertyNameRuleException(EntityFieldType fieldType,
                                      String fieldName,
                                      EntityType entityType,
                                      String entityName,
                                      int maxNameLength) {
        super(fieldType, fieldName, entityType, entityName, maxNameLength);
    }

    public static PropertyNameRuleException of(XmlModelClassProperty property, int maxNameLength) {
        return new PropertyNameRuleException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.CLASS,
                property.getModelClass().getName(),
                maxNameLength
        );
    }

    public static PropertyNameRuleException of(XmlModelInterfaceProperty property, int maxNameLength) {
        return new PropertyNameRuleException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.INTERFACE,
                property.getModelInterface().getName(),
                maxNameLength
        );
    }
}
