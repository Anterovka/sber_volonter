package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlQueryProperty;
import com.sbt.mg.data.model.XmlQueryParam;

/**
 * Exception: The name of the property is a reserved word in the Java language
 */
public class UserQueryTagNameAsJavaReservedWordException extends FieldNameAsJavaReservedWordException {
    private UserQueryTagNameAsJavaReservedWordException(String fieldType, String fieldName, String entityType, String entityName) {
        super(fieldType, fieldName, entityType, entityName);
    }

    public static UserQueryTagNameAsJavaReservedWordException of(XmlQueryParam param) {
        return new UserQueryTagNameAsJavaReservedWordException(
                "tag param",
                param.getName(),
                "query",
                param.getXmlQuery().getName()
        );
    }

    public static UserQueryTagNameAsJavaReservedWordException of(XmlQueryProperty property) {
        return new UserQueryTagNameAsJavaReservedWordException(
                "tag column",
                property.getName(),
                "query",
                property.getXmlQuery().getName()
        );
    }
}
