package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlQueryProperty;
import com.sbt.mg.data.model.XmlQueryParam;

/**
 * Exception: The property name is reserved for the needs of the SDK
 */
public class UserQueryTagNameAsLocalReservedWordException extends FieldNameAsLocalReservedWordException {
    private UserQueryTagNameAsLocalReservedWordException(String fieldType, String fieldName, String entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static UserQueryTagNameAsLocalReservedWordException of(XmlQueryParam param) {
        return new UserQueryTagNameAsLocalReservedWordException(
                "tag param",
                param.getName(),
                "query",
                param.getXmlQuery().getName()
        );
    }

    public static UserQueryTagNameAsLocalReservedWordException of(XmlQueryProperty property) {
        return new UserQueryTagNameAsLocalReservedWordException(
                "tag column",
                property.getName(),
                "query",
                property.getXmlQuery().getName()
        );
    }
}
