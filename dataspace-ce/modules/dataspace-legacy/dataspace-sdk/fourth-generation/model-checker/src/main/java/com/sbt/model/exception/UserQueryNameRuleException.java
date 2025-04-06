package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlQuery;

public class UserQueryNameRuleException extends EntityNameRuleException {
    public UserQueryNameRuleException(XmlQuery xmlQuery, int maxNameLength) {
        super(xmlQuery.getName(), "query", maxNameLength);
    }
}
