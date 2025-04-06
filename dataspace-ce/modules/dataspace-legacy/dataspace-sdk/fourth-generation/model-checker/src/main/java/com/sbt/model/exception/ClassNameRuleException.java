package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClass;

public class ClassNameRuleException extends EntityNameRuleException {
    public ClassNameRuleException(XmlModelClass modelClass, int maxNameLength) {
        super(modelClass.getName(), "class", maxNameLength);
    }
}
