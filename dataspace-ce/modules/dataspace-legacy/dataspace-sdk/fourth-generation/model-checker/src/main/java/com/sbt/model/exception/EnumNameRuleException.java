package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelClassEnum;

public class EnumNameRuleException extends EntityNameRuleException {
    public EnumNameRuleException(XmlModelClassEnum modelEnum, int maxNameLength) {
        super(modelEnum.getName(), "enum класса", maxNameLength);
    }
}
