package com.sbt.model.exception;

import com.sbt.mg.data.model.XmlModelInterface;

public class InterfaceNameRuleException extends EntityNameRuleException {
    public InterfaceNameRuleException(XmlModelInterface modelInterface, int maxNameLength) {
        super(modelInterface.getName(), "interface", maxNameLength);
    }
}
