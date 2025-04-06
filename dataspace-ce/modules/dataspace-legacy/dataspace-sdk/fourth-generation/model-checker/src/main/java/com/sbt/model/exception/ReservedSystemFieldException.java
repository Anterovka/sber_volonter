package com.sbt.model.exception;

import com.sbt.mg.exception.checkmodel.CheckXmlModelException;

import java.util.List;

public class ReservedSystemFieldException extends CheckXmlModelException {
    public ReservedSystemFieldException(String className, List<String> propertyNames) {
        super(join("In the class", className, "properties with names", propertyNames, "reserved by the system are defined"),
            "Replace the property name with another one");
    }
}
