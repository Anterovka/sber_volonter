package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelInterfaceProperty;
import com.sbt.model.common.EntityFieldType;
import com.sbt.model.common.EntityType;

/**
 * Exception: The class name is already set in the base module model
 */
public class PropertyTypeRuleException extends FiledTypeRuleException {
    private PropertyTypeRuleException(EntityFieldType fieldType, String fieldName, EntityType entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static PropertyTypeRuleException of(XmlModelClassProperty property) {
        return new PropertyTypeRuleException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.CLASS,
                property.getModelClass().getName()
        );
    }

    public static PropertyTypeRuleException of(XmlModelInterfaceProperty property) {
        return new PropertyTypeRuleException(
                EntityFieldType.PROPERTY,
                property.getName(),
                EntityType.INTERFACE,
                property.getModelInterface().getName()
        );
    }
}
