package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlQueryProperty;
import com.sbt.mg.data.model.XmlQueryParam;

/**
 * Exception: The class name is already set in the base module model
 */
public class UserQueryTagRuleException extends FiledNameRuleException {
    private UserQueryTagRuleException(String fieldType,
                                      String fieldName,
                                      String entityType,
                                      String entityName,
                                      int maxNameLength) {
        super(fieldType, fieldName, entityType, entityName, maxNameLength);
    }

    public static UserQueryTagRuleException of(XmlQueryParam param, int maxNameLength) {
        return new UserQueryTagRuleException(
                "tag param",
                param.getName(),
                "query",
                param.getXmlQuery().getName(),
                maxNameLength
        );
    }

    public static UserQueryTagRuleException of(XmlQueryProperty property, int maxNameLength) {
        return new UserQueryTagRuleException(
                "tag column",
                property.getName(),
                "query",
                property.getXmlQuery().getName(),
                maxNameLength
        );
    }
}
